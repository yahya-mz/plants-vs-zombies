package com.pvz.plantsvszombies.Multiplayer.Network;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.pvz.plantsvszombies.Multiplayer.Events.SharedEvent;

/**
 * Manages network communication for multiplayer functionality
 */
public abstract class NetworkManager {
    private static final int DEFAULT_PORT = 12345;
    private final ConcurrentLinkedQueue<SharedEvent> incomingEvents;
    private final ConcurrentLinkedQueue<SharedEvent> outgoingEvents;
    private final CopyOnWriteArrayList<Consumer<SharedEvent>> eventListeners;

    private boolean isRunning = false;
    protected Thread networkThread;

    public NetworkManager() {
        this.incomingEvents = new ConcurrentLinkedQueue<>();
        this.outgoingEvents = new ConcurrentLinkedQueue<>();
        this.eventListeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Add a listener for incoming events
     */
    public void addEventListener(Consumer<SharedEvent> listener) {
        eventListeners.add(listener);
    }

    /**
     * Remove an event listener
     */
    public void removeEventListener(Consumer<SharedEvent> listener) {
        eventListeners.remove(listener);
    }

    /**
     * Send an event to the network
     */
    public void sendEvent(SharedEvent event) {
        outgoingEvents.offer(event);
    }

    protected void notifyIncomingEvent(SharedEvent event) {
        incomingEvents.offer(event);
    }

    /**
     * Get the next incoming event (non-blocking)
     */
    protected SharedEvent getNextEvent() {
        return incomingEvents.poll();
    }
    protected SharedEvent getNextOutgoingEvent() {
        return outgoingEvents.poll();
    }

    /**
     * Check if there are incoming events
     */
    public boolean hasIncomingEvents() {
        return !incomingEvents.isEmpty();
    }

    /**
     * Process all incoming events and notify listeners
     */
    public void processEvents() {
        SharedEvent event;
        while ((event = incomingEvents.poll()) != null) {
            final SharedEvent finalEvent = event;
            eventListeners.forEach(listener -> {
                try {
                    listener.accept(finalEvent);
                } catch (Exception e) {
                    System.err.println("Error processing event: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Start the network manager
     */
    public void start() {
        if (!isRunning) {
            isRunning = true;
            startNetworkThread();
        }
    }

    /**
     * Stop the network manager
     */
    public void stop() {
        isRunning = false;
        if (networkThread != null) {
            networkThread.interrupt();
        }
    }

    /**
     * Method to be overridden by server and client managers
     */
    protected abstract void startNetworkThread();

    protected boolean hasOutgoingEvents() {
        return !outgoingEvents.isEmpty();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
