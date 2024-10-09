package dev.httpmarco.osgan.networking;

import io.netty5.util.AttributeKey;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public final class CommunicationAttributes {

    public static final AttributeKey<UUID> ID = AttributeKey.valueOf("id");

}
