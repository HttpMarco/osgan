package dev.httpmarco.osgan.networking.client;


public final class ReconnectQueue extends Thread {

    private static final long RECONNECT_TIMEOUT = 5000;
    private final NettyClient nettyClient;

    public ReconnectQueue(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }

    @Override
    public void run() {
        while (true) {
            if (!nettyClient.isConnected()) {
                System.out.println("Reconnecting...");
                nettyClient.connect();
            }
            try {
                sleep(RECONNECT_TIMEOUT);
            } catch (InterruptedException ignored) {
            }
        }
    }
}