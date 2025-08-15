package com.pvz.plantsvszombies.Multiplayer.Events;

import com.pvz.plantsvszombies.Domain.Entities.MapBlock;
import com.pvz.plantsvszombies.Domain.Entities.MapGameObject;

import java.util.List;

/**
 * Event for when a client is ready with their plant selection
 */
public class ClientReadyEvent extends SharedEvent {
    private String clientId;
    private List<String> selectedPlants; // Plant type names as strings
    private boolean isReady;
    private MapGameObject map;

    public ClientReadyEvent() {
        super();
    }

    public ClientReadyEvent(long gameTick, String clientId, List<String> selectedPlants,
                            MapGameObject map) {
        super(gameTick);
        this.clientId = clientId;
        this.selectedPlants = selectedPlants;
        this.isReady = true;
        this.map = map;
    }

    @Override
    public String getEventType() {
        return "client_ready";
    }

    // Getters and setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getSelectedPlants() {
        return selectedPlants;
    }

    public void setSelectedPlants(List<String> selectedPlants) {
        this.selectedPlants = selectedPlants;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public MapGameObject getMapObject() {
        return map;
    }
}