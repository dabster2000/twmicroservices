package dk.trustworks.invoicewebui.web;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hans on 16/07/2017.
 */
public class Broadcaster implements Serializable {

    public interface BroadcastListener {
        void receiveBroadcast(String message);
    }

}
