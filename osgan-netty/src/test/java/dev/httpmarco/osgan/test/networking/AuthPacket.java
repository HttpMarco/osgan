package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.Packet;

import java.util.UUID;

public class AuthPacket implements Packet {

    private String token;
    private UUID uuid;
    private Long coins;
    private Integer level;
    private Boolean isAdmin;
    private Double balance;
    private Float health;
    private Short age;
    private Byte ageByte;

    public AuthPacket(String token, UUID uuid, Long coins, Integer level, Boolean isAdmin, Double balance, Float health, Short age, Byte ageByte) {
        this.token = token;
        this.uuid = uuid;
        this.coins = coins;
        this.level = level;
        this.isAdmin = isAdmin;
        this.balance = balance;
        this.health = health;
        this.age = age;
        this.ageByte = ageByte;
    }
}
