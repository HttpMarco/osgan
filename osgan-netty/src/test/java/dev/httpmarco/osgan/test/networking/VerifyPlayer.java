package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.annotation.PacketIncludeObject;

import java.util.Collection;
import java.util.UUID;

@PacketIncludeObject
public class VerifyPlayer {

    private UUID uuid;
    private String tsId;
    private String dcId;
    private String rank;
    private boolean toggleBot;
    private String forumAccount;
    private String userId;
    private String[] roles;
    private Collection<String> usernames;

    public VerifyPlayer(UUID uuid, String tsId, String dcId, String rank, boolean toggleBot, String forumAccount, String userId, String[] roles, Collection<String> usernames) {
        this.uuid = uuid;
        this.tsId = tsId;
        this.dcId = dcId;
        this.rank = rank;
        this.toggleBot = toggleBot;
        this.forumAccount = forumAccount;
        this.userId = userId;
        this.roles = roles;
        this.usernames = usernames;
    }

    public String getTsId() {
        return tsId;
    }
}
