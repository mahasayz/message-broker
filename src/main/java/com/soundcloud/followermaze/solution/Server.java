package com.soundcloud.followermaze.solution;

import java.io.*;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by Mahbub on 12/4/2016.
 */
public class Server extends Thread {

    private ConcurrentSkipListMap<Integer, Event> eventMap;

    public Server(ConcurrentSkipListMap eventMap) {
        this.eventMap = eventMap;
    }

    public void run() {
        try{
            ServerSocket server = new ServerSocket(9090);

            while(true) {
                Socket event = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(event.getInputStream()));

                String input;
                while ((input = in.readLine()) != null) {
                    String[] str = input.split("\\|");
                    if (str.length == 4) {
                        eventMap.put(Integer.parseInt(str[0]), new Event(Integer.parseInt(str[0]),
                                str[1], Integer.parseInt(str[2]), Integer.parseInt(str[3]), input));
                    } else if (str.length == 3) {
                        eventMap.put(Integer.parseInt(str[0]), new Event(Integer.parseInt(str[0]),
                                str[1], Integer.parseInt(str[2]), -1, input));
                    } else {
                        eventMap.put(Integer.parseInt(str[0]), new Event(Integer.parseInt(str[0]),
                                str[1], -1, -1, input));
                    }
//                    System.out.println("Event Map size : " + eventMap.keySet().size() + " with " + input);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
