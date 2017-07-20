package dk.trustworks.web;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hans on 16/07/2017.
 */
public class Broadcaster implements Serializable {
    static ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public interface BroadcastListener {
        void receiveBroadcast(String message);
    }

    private static LinkedList<BroadcastListener> listeners =
            new LinkedList<BroadcastListener>();

    public static synchronized void register(
            BroadcastListener listener) {
        listeners.add(listener);
    }

    public static synchronized void unregister(
            BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcast(
            final String message) {
        for (final BroadcastListener listener: listeners)
            executorService.execute(() -> listener.receiveBroadcast(message));
    }
}
