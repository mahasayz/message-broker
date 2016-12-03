/**
 * MessageBusSocketImpl.java  1.00 3.14.98 Michael Shoffner
 *
 * Copyright (c) 1998 Michael Shoffner, Merlin Hughes. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * for non-commercial purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 *
 * http://prominence.com/                          shoffner@prominence.com
 */

package com.soundcloud.followermaze;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessageBusSocketImpl implements MessageBus, Runnable {

  protected String brokerName;
  protected Socket broker;

  protected int port;
  protected Thread processor;
  protected ObjectInputStream objectIn;
  protected ObjectOutputStream objectOut;
  
  public MessageBusSocketImpl (String name, int port) {
    this.brokerName = name;
    this.port = port;
  }

  public void initBroker () throws IOException {
    broker = new Socket (brokerName, port);
    try {
      objectOut = new ObjectOutputStream (
        new BufferedOutputStream (broker.getOutputStream ()));
      objectOut.flush ();
      objectIn = new ObjectInputStream (
        new BufferedInputStream (broker.getInputStream ()));
      InitMsg initMsg = (InitMsg) objectIn.readObject ();
      Vector channelNames = initMsg.getChannelNames ();
      Enumeration names = channelNames.elements ();
      while (names.hasMoreElements ()) {
        String name = (String) names.nextElement ();
        Channel.channelAdded (name);
      }
      processor = new Thread (this);
      processor.start ();
    } catch (IOException ex) {
      try {
        broker.close ();
      } catch (IOException ignored) {
      }
      throw ex;
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace ();
      try {
        broker.close ();
      } catch (IOException ignored) {
      }
    }
  }
  
  public String getBrokerName () {
    return brokerName;
  }

  public synchronized void addChannel (String name) throws IOException {
    objectOut.writeObject (new ChannelAddedMsg (name));
    objectOut.flush ();
  }

  public synchronized void publishChannelMessage (String name, Object message) throws IOException {
    objectOut.writeObject (new ChannelUpdateMsg (name, message));
    objectOut.flush ();
  }
  
  public synchronized void stop () {
    if (processor != null) {
      try {
        objectOut.writeObject (new QuitMsg ());
        objectOut.flush ();
      } catch (IOException ignored) {
      }
      processor = null;
    }
  }

  public void run () {
    // does not try to reestablish socket if connection is lost 
    try {
      while (Thread.currentThread () == processor)
        processMsgs ();
    } catch (Exception ex) {
      ex.printStackTrace ();
    } finally {
      try {
        broker.close ();
      } catch (IOException ignored) {}
    }
    processor = null;
  }

  void processMsgs () throws IOException, ClassNotFoundException {
    BusMsg msg = (BusMsg) objectIn.readObject ();
    if (msg instanceof ChannelAddedMsg) {
      String name = ((ChannelAddedMsg) msg).getName ();
      Channel.channelAdded (name);
    } else if (msg instanceof ChannelUpdateMsg) {
      String name = ((ChannelUpdateMsg) msg).getName ();
      Object message = ((ChannelUpdateMsg) msg).getMessage ();
      Channel.channelMessageReceived (name, message);
    } else {
      System.out.println ("Unknown message: " + msg);
    }
  }
}

  
  

