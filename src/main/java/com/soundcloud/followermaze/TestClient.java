package com.soundcloud.followermaze;

import com.soundcloud.followermaze.web.MessageBusListAddElementMsg;
import com.soundcloud.followermaze.web.MessageBusListMsg;
import com.sun.jmx.remote.internal.ClientListenerInfo;

import java.io.IOException;

/**
 * Created by Mahbub on 12/3/2016.
 */
public class TestClient implements ChannelListener {

    Channel whiteboard;
    int lastID = 0;
    String clientID = String.valueOf((int) Math.random() * 1000000);

    public TestClient() {
        try {
            MessageBus mb = new MessageBusSocketImpl("localhost", 5001);
            Channel.setMessageBus(mb);
            whiteboard = Channel.subscribe("whiteboard_channel_1", this);
        } catch (IOException e) {
            System.out.println("Setup FAILED!");
            e.printStackTrace();
        }
    }

    public synchronized void addElement(Object element) {
        try {
            MessageBusListAddElementMsg m = new MessageBusListAddElementMsg(clientID, String.valueOf(lastID++), element);
            whiteboard.publish(m);
        } catch (IOException e) {
            System.out.println("Publish FAILED!");
            e.printStackTrace();
        }
    }


    @Override
    public void messageReceived(Channel channel, Object message) {
        if (channel.getName().equals("whiteboard_channel_1")) {
            MessageBusListMsg pMsg = (MessageBusListMsg) message;
            synchronized (this) {
                if (pMsg instanceof MessageBusListAddElementMsg) {
                    Object msg = pMsg.getMessage();
                    System.out.println("Message received: " + msg);
                } else {
                    System.out.println("Unknown message received" + pMsg);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestClient client = new TestClient();

        client.addElement("Test1");
        Thread.sleep(5000);
        System.exit(0);
    }
}
