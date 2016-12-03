/**
 * Channel.java  1.00 3.14.98 Michael Shoffner
 *
 * Copyright (c) 1998 Michael Shoffner. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * for non-commercial purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 *
 * http://prominence.com/                shoffner@prominence.com
 */

package com.soundcloud.followermaze;

import java.io.*;
import java.util.*;

public class Channel {

  protected static boolean busSet = false;
  protected static MessageBus bus;
  protected static Hashtable channels = new Hashtable ();
  protected static Vector channelsUpdateListeners = new Vector ();

  public static synchronized void setMessageBus (MessageBus mb) throws IOException {
    if (!busSet) {
      bus = mb;
      bus.initBroker ();
      busSet = true;
    } else
      System.out.println ("Can't set MessageBus more than once per runtime!");
  }

  public static String getBrokerName () {
    return bus.getBrokerName ();
  }
  
  public static Enumeration getChannelNames () {
    return channels.keys ();
  }

  public static synchronized Channel subscribe (String name, ChannelListener cl) throws IOException {
    Channel ch;
    if (channels.containsKey (name))
      ch = (Channel) channels.get (name);
    else {
      bus.addChannel (name);
      ch = new Channel (name);
      channels.put (name, ch);
    }
    ch.subscribe (cl);
    return ch;
  }

  // called by clients to register ChannelsUpdateListener
  public static void subscribeChannelsUpdates (ChannelsUpdateListener cul) {
    channelsUpdateListeners.addElement (cul);
  }

  // called by clients to de-register ChannelsUpdateListener
  public static void unsubscribeChannelsUpdates (ChannelsUpdateListener cul) {
    channelsUpdateListeners.removeElement (cul);
  }
  
  // called by MessageBus, broadcasts to ChannelsUpdateListeners
  protected static void channelAdded (String name) {
    if (!channels.containsKey (name)) {
      Vector l;
      synchronized (new Channel ("").getClass ()) {
        channels.put (name, new Channel (name));
        l = (Vector) channelsUpdateListeners.clone ();
      }
      Enumeration chs = l.elements ();
      while (chs.hasMoreElements ()) {
        ChannelsUpdateListener cul;
        cul = (ChannelsUpdateListener) chs.nextElement ();
        try {
          cul.channelAdded (name);
        } catch (Exception ex) {
          ex.printStackTrace ();
        }
      }
    }
  }

  // called my MessageBus
  protected static void channelMessageReceived (String name, Object message) {
    Channel target = (Channel) channels.get (name);
    if (target != null)
      target.messageReceived (message);
  }
  
  // called by Channel instances
  protected static void publish (Channel channel, Object message) throws IOException {
    String name = channel.getName ();
    if (channels.containsKey (name))
      bus.publishChannelMessage (name, message);
  }
  
  /*
   *  Channel instance members
   */
  
  protected String name;
  protected Vector listeners = new Vector ();
  protected Vector sentMessages = new Vector ();
  protected Vector messageSenders = new Vector ();
  
  protected Channel (String name) {
    this.name = name;
  }
  
  public void subscribe (ChannelListener cl) {
    listeners.addElement (cl);
  }
  
  public String getName () {
    return name;
  }

  public void publish (Object message) throws IOException {
    Channel.publish (this, message);
  }
  
  public void unsubscribe (ChannelListener cl) {
    listeners.removeElement (cl);
  }

  // called by Channel.channelMessageReceived ()
  protected void messageReceived (Object message) {
    Vector l;
    synchronized (this) {
      l = (Vector) listeners.clone ();
    }
    Enumeration cls = l.elements ();
    while (cls.hasMoreElements ()) {
      ChannelListener cl = (ChannelListener) cls.nextElement ();
      try{
        cl.messageReceived (this, message);
      } catch (Exception ex) {
        ex.printStackTrace ();
      }
    }
  }
}


