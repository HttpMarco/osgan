package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.files.annotations.ConfigExclude;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Packet {
    @ConfigExclude
    private final CodecBuffer buffer;

    public Packet() {
        this.buffer = CodecBuffer.allocate();
    }
}
