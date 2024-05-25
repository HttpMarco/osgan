package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.codec.CodecBuffer;

public abstract class Packet {

    public abstract void onRead(CodecBuffer buffer);

    public abstract void onWrite(CodecBuffer buffer);

}
