package dev.httpmarco.osgan.networking.client;

public enum CommunicationClientAction {

    // if client successfully connected to the server
    CONNECTED,
    // if port or hostname or wrong or unavailable
    FAILED,

    // if client close the connection
    CLIENT_DISCONNECT,
    // if server close the connection
    DISCONNECTED

}
