package com.soundcloud.followermaze.solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mahbub on 12/4/2016.
 */

class UserThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ConcurrentHashMap<Integer, PrintWriter> channelMap;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Boolean>> followerMap;

    public UserThread(Socket socket, ConcurrentHashMap channelMap, ConcurrentHashMap followerMap) {
        this.socket = socket;
        this.channelMap = channelMap;
        this.followerMap = followerMap;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        String input;
        try {
            while ((input = in.readLine()) != null) {
                System.out.println(input);
                channelMap.put(Integer.parseInt(input), out);
                followerMap.put(Integer.parseInt(input), new ConcurrentHashMap<>());
                System.out.println("Channel size : " + channelMap.keySet().size() + ", FollowerMap size : " + followerMap.keySet().size());
            }

//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Client extends Thread {

    private ConcurrentHashMap<Integer, Socket> channelMap;
    private ConcurrentHashMap<Integer, LinkedList<Integer>> followerMap;

    public Client(ConcurrentHashMap channelMap, ConcurrentHashMap followerMap) {
        this.channelMap = channelMap;
        this.followerMap = followerMap;
    }

    public void run() {

        try{
            ServerSocket server = new ServerSocket(9099);

            while(true) {
                Socket event = server.accept();
                new UserThread(event, channelMap, followerMap).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
