/**
 * MessageBusBrokerSocketImpl.java  1.00 3.14.98 Michael Shoffner
 *
 * Copyright (c) 1998 Merlin Hughes, Michael Shoffner. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * for non-commercial purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 *
 * http://prominence.com/                  shoffner@prominence.com
 */

package com.soundcloud.followermaze;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessageBusBrokerSocketImpl implements Runnable {
  
  static Vector channels = new Vector ();
  static Vector streams = new Vector ();

  Socket socket;

  public MessageBusBrokerSocketImpl (Socket socket) {
    System.out.println ("Accepted from " + socket.getInetAddress () + ".");
    this.socket = socket;
  }

  public void run () {
    try {
      ObjectOutputStream objectOut = new ObjectOutputStream (
        new BufferedOutputStream (socket.getOutputStream ()));
      objectOut.flush ();
      ObjectInputStream objectIn = new ObjectInputStream (
        new BufferedInputStream (socket.getInputStream ()));
      handle (objectIn, objectOut);
    } catch (Exception ex) {
      ex.printStackTrace ();
    } finally {
      System.out.println ("Disconnected from " + socket.getInetAddress () + ".");
      try {
        socket.close ();
      } catch (IOException ignored) {
      }
    }
  }

  void handle (ObjectInputStream objectIn, ObjectOutputStream objectOut) throws IOException, ClassNotFoundException {
    try {
      synchronized (streams) {
        streams.addElement (objectOut);
        objectOut.writeObject (new InitMsg (channels));
        objectOut.flush ();
      }
      processMsgs (objectIn);
    } finally {
      streams.removeElement (objectOut);
    }
  }

  void processMsgs (ObjectInputStream objectIn) throws IOException, ClassNotFoundException {
    while (true) {
      BusMsg msg = (BusMsg) objectIn.readObject ();
      System.out.println ("Received " + msg.getClass ().getName () + " from " + socket.getInetAddress () + ".");
      if (msg instanceof QuitMsg) {
        break;
      } else if (msg instanceof ChannelAddedMsg) {
        String name = (String) ((ChannelAddedMsg) msg).getName ();
        synchronized (streams) {
          channels.addElement (name);
          broadcastMsg (new ChannelAddedMsg (name));
        }
      } else if (msg instanceof ChannelUpdateMsg) {
        String name = (String) ((ChannelUpdateMsg) msg).getName ();
        Object message = ((ChannelUpdateMsg) msg).getMessage ();
        synchronized (streams) {
          broadcastMsg (new ChannelUpdateMsg (name, message));
        }
      } else {
        System.out.println ("Unknown message: " + msg);
      }
    }
  }

  static void addStream (ObjectOutputStream out) {
    streams.addElement (out);
  }

  static void removeStream (ObjectOutputStream out) {
    streams.removeElement (out);
  }

  static void broadcastMsg (BusMsg msg) {
    System.out.println ("Broadcast " + msg.getClass ().getName () + " to " + streams.size () + " recipients.");
    for (int i = 0; i < streams.size (); ++ i) {
      ObjectOutputStream out = (ObjectOutputStream) streams.elementAt (i);
      try {
        out.writeObject (msg);
        out.flush ();
      } catch (IOException ex) {
        ex.printStackTrace ();
      }
    }
  }
  
  public static void main (String[] args) throws IOException {
    if (args.length != 1)
      throw new IllegalArgumentException ("Syntax: MessageBusBroker <port>");
    int port = Integer.parseInt (args [0]);
    ServerSocket serverSocket = new ServerSocket (port);
    System.out.println ("");
    System.out.println ("MessageBus Broker (Socket Impl) started on port " + port); 
    while (true) {
      try {
        MessageBusBrokerSocketImpl handler = new MessageBusBrokerSocketImpl (serverSocket.accept ());
        new Thread (handler).start ();
      } catch (IOException ex) {
        ex.printStackTrace ();
      }
    }
  }
}
