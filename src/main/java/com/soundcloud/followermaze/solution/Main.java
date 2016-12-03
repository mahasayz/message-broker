package com.soundcloud.followermaze.solution;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Mahbub on 12/4/2016.
 */

class Workers extends Thread {

    private ConcurrentSkipListMap<Integer, Event> eventMap;
    private ConcurrentHashMap<Integer, PrintWriter> clientMap;
    private ConcurrentHashMap<Integer, List<Integer>> followerMap;
    private AtomicInteger counter;

    public Workers(ConcurrentSkipListMap eventMap, ConcurrentHashMap clientMap, ConcurrentHashMap followerMap, AtomicInteger counter) {
        this.eventMap = eventMap;
        this.clientMap = clientMap;
        this.followerMap = followerMap;
        this.counter = counter;
    }

    public void run() {
        while(true) {
            while (eventMap.keySet().size() > 0) {
                Event event = eventMap.firstEntry().getValue();
                if (event.getId() != counter.get())
                    continue;
                System.out.println("Handling event with Id " + event.getId());
                switch (event.getType()) {
                    case "F": {
                        PrintWriter socket = clientMap.getOrDefault(event.getToId(), null);
                        List list = followerMap.get(event.getFromId());
                        if (list == null) {
                            list = new LinkedList<Integer>();
                        }
                        list.add(event.getToId());
                        followerMap.put(event.getFromId(), list);
                        if (socket != null) {
                            socket.println(event.getMessage());
                        }
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "U": {
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "B": {
                        for (PrintWriter socket : clientMap.values()) {
                            socket.println(event.getMessage());
                        }
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "P": {
                        PrintWriter socket = clientMap.getOrDefault(event.getToId(), null);
                        if (socket != null) {
                            socket.println(event.getMessage());
                        }
                        eventMap.remove(event.getId());
                        System.out.println("Counter : " + counter.incrementAndGet());
                        break;
                    }
                    case "S": {
                        for (Integer toId : followerMap.getOrDefault(event.getFromId(), Collections.<Integer>emptyList())) {
                            PrintWriter socket = clientMap.getOrDefault(toId, null);
                            if (socket == null) {
                                System.out.println("Couldn't get " + toId + " from channelMap");
                                continue;
                            }
                            socket.println(event.getMessage());
                        }
                        System.out.println("Counter : " + counter.incrementAndGet());
                        eventMap.remove(event.getId());
                        break;
                    }
                    default:
                        System.out.print("Got a weird thing");
                }
            }
        }
    }
}

public class Main {

    private ConcurrentSkipListMap<Integer, Event> eventMap;
    private ConcurrentHashMap<Integer, PrintWriter> clientMap;
    private ConcurrentHashMap<Integer, List<Integer>> followerMap;
    private ExecutorService executors = Executors.newFixedThreadPool(16);
    private AtomicInteger counter;

    public void init() throws InterruptedException {
        eventMap = new ConcurrentSkipListMap<>();
        clientMap = new ConcurrentHashMap<>();
        followerMap = new ConcurrentHashMap<>();
        Server server = new Server(eventMap);
        server.start();
        Client client = new Client(clientMap, followerMap);
        client.start();
        counter = new AtomicInteger(1);
        executors.execute(new Workers(eventMap, clientMap, followerMap, counter));
    }

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        main.init();
    }

}
