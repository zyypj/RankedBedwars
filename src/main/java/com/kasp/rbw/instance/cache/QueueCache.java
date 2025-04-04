package com.kasp.rbw.instance.cache;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Queue;

import java.util.HashMap;
import java.util.Map;

public class QueueCache {

    private static final HashMap<String, Queue> queues = new HashMap<>();

    public static Queue getQueue(String ID) {
        return queues.get(ID);
    }

    public static void addQueue(Queue queue) {
        queues.put(queue.getID(), queue);

        Config.debug("Queue " + queue.getID() + " foi carregado na memoria");
    }

    public static void removeQueue(Queue queue) {
        queues.remove(queue.getID());
    }

    public static boolean containsQueue(String ID) {
        return queues.containsKey(ID);
    }

    public static void initializeQueue(String ID, Queue queue) {
        if (!containsQueue(ID))
            addQueue(queue);

        getQueue(ID);
    }

    public static Map<String, Queue> getQueues() {
        return queues;
    }
}
