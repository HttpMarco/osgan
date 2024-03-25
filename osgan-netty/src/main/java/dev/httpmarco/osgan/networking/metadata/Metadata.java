package dev.httpmarco.osgan.networking.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class Metadata {

    // connection host address
    private String hostname;
    // connection port
    private int port;

}
