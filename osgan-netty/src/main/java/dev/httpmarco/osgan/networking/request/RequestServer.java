package dev.httpmarco.osgan.networking.request;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.CommunicationFuture;
import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.packet.*;
import dev.httpmarco.osgan.networking.packet.BadRequestPacket;
import dev.httpmarco.osgan.networking.packet.RegisterResponderPacket;
import dev.httpmarco.osgan.networking.packet.RequestPacket;
import dev.httpmarco.osgan.networking.packet.RequestResponsePacket;
import dev.httpmarco.osgan.networking.server.CommunicationServerAction;
import io.netty5.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Accessors(fluent = true)
@Getter
public abstract class RequestServer extends CommunicationComponent<CommunicationServerAction> implements Request, Response {
    private static final Random RANDOM = new Random();

    private final Map<UUID, CommunicationFuture<? extends Packet>> requests = new HashMap<>();
    private final Map<String, Function<CommunicationProperty, Packet>> responders = new HashMap<>();

    private final Map<UUID, PendingRequest> pending = new HashMap<>();

    private final Map<String, List<Channel>> registeredResponders = new HashMap<>();
    private final Map<Channel, List<String>> respondersByChannel = new HashMap<>();

    public RequestServer(int bossGroupThreads, String hostname, int port) {
        super(bossGroupThreads, hostname, port);

        listen(RequestPacket.class, (transmit, packet) -> {
            System.out.println("Received request: " + packet.uuid() +
                    " for: " + packet.id() +
                    " from: " + transmit.channel().remoteAddress().toString().replaceFirst("/", ""));

            if (hasResponder(packet.id())) {
                transmit.sendPacket(new RequestResponsePacket(packet.uuid(), buildResponse(packet)));
                return;
            }

            if (this.registeredResponders.containsKey(packet.id())) {
                this.pending.put(packet.uuid(), new PendingRequest(
                        transmit,
                        packet.id(),
                        packet.uuid(),
                        System.currentTimeMillis()
                ));

                Objects.requireNonNull(this.pickRandomResponderChannel(packet.id())).writeAndFlush(packet);
                return;
            }

            transmit.sendPacket(new BadRequestPacket(packet.uuid(), "No responder registered!"));

            System.out.println("No responder registered for: " + packet.id());
        });

        listen(BadRequestPacket.class, (transmit, packet) -> {
            if (requests.containsKey(packet.uuid())) {
                completeRequest(packet.uuid(), null);

                this.requests.remove(packet.uuid());
            } else if (pending.containsKey(packet.uuid())) {
                pending.get(packet.uuid()).transmit().sendPacket(packet);
                pending.remove(packet.uuid());

                System.out.println("Received bad request: " + packet.uuid() +
                        " from: " + transmit.channel().remoteAddress().toString().replaceFirst("/", ""));
            }
        });

        listen(RequestResponsePacket.class, (transmit, packet) -> {
            if (requests.containsKey(packet.uuid())) {
                completeRequest(packet.uuid(), packet.buildPacket());

                this.requests.remove(packet.uuid());
            } else if (pending.containsKey(packet.uuid())) {
                pending.get(packet.uuid()).transmit().sendPacket(packet);
                pending.remove(packet.uuid());
            }

            System.out.println("Received response: " + packet.uuid() +
                    " from: " + transmit.channel().remoteAddress().toString().replaceFirst("/", ""));
        });

        listen(RegisterResponderPacket.class, (transmit, packet) -> {
            var id = packet.id();

            if (!this.registeredResponders.containsKey(id)) {
                this.registeredResponders.put(id, new ArrayList<>());
            }

            if (!this.respondersByChannel.containsKey(transmit.channel())) {
                this.respondersByChannel.put(transmit.channel(), new ArrayList<>());
            }

            this.registeredResponders.get(id).add(transmit.channel());
            this.respondersByChannel.get(transmit.channel()).add(id);

            System.out.println("Registered responder: " + id + " from: " + transmit.channel().remoteAddress().toString().replaceFirst("/", ""));
        });

        clientAction(CommunicationServerAction.CLIENT_DISCONNECT, transmit -> {
            if (!this.respondersByChannel.containsKey(transmit.channel())) {
                return;
            }

            respondersByChannel().get(transmit.channel()).forEach(s -> {
                this.registeredResponders.get(s).remove(transmit.channel());

                if (this.registeredResponders.get(s).isEmpty()) {
                    this.registeredResponders.remove(s);

                    System.out.println("Unregistered responder: " + s + " from: " + transmit.channel().remoteAddress().toString().replaceFirst("/", ""));
                }
            });

            respondersByChannel().remove(transmit.channel());
        });
    }

    @Override
    public void completeRequest(UUID uuid, @Nullable Packet packet) {
        if (hasRequest(uuid)) {
            ((CommunicationFuture<Packet>) this.requests.get(uuid)).complete(packet);
            this.requests.remove(uuid);
        }
    }

    @Override
    public <P extends Packet> CompletableFuture<P> requestAsync(String id, Class<P> packet, CommunicationProperty property) {
        var future = new CommunicationFuture<P>();
        var uuid = UUID.randomUUID();

        this.requests.put(uuid, future);

        if (this.responders.containsKey(id)) {
            this.completeRequest(uuid, buildResponse(new RequestPacket(id, uuid, true, property)));
        } else if (this.registeredResponders.containsKey(id)) {
            Objects.requireNonNull(this.pickRandomResponderChannel(id)).writeAndFlush(new RequestPacket(id, uuid, true, property));
        } else {
            this.requests.remove(uuid);
            System.out.println("No responder registered locally and none found on any other service!");
        }

        return future;
    }

    @Override
    public void registerResponder(String id, Function<CommunicationProperty, Packet> function) {
        this.responders.put(id, function);

        System.out.println("Registered local responder: " + id);
    }

    @Override
    public void unregisterResponder(String id) {
        this.responders.remove(id);

        System.out.println("Unregistered local responder: " + id);
    }

    @Override
    public Packet buildResponse(RequestPacket requestPacket) {
        return responders.get(requestPacket.id()).apply(requestPacket.property());
    }

    private Channel pickRandomResponderChannel(String id) {
        if (!this.registeredResponders.containsKey(id)) {
            return null;
        }

        var responders = this.registeredResponders.get(id);

        if (responders.size() == 1) {
            return responders.get(0);
        }

        return responders.get(RANDOM.nextInt(responders.size()));
    }
}
