package com.pvz.plantsvszombies.Multiplayer.Events;

/**
 * Event for client status updates (progress, health, etc.)
 */
public class ClientStatusEvent extends SharedEvent {
    private String clientId;
    private int zombiesRemaining;
    private ClientStatus status;

    public enum ClientStatus {
        CONNECTED,
        PLAYING,
        LOST,
        WON,
        DISCONNECTED
    }

    public ClientStatusEvent() {
        super();
    }

    public ClientStatusEvent(long gameTick, String clientId,
                             int zombiesRemaining, ClientStatus status) {
        super(gameTick);
        this.clientId = clientId;
        this.zombiesRemaining = zombiesRemaining;
        this.status = status;
    }

    @Override
    public String getEventType() {
        return "client_status";
    }

    // Getters and setters
    public String getClientId() {
        return clientId;
    }

    public int getZombiesRemaining() {
        return zombiesRemaining;
    }


    public ClientStatus getStatus() {
        return status;
    }

}
