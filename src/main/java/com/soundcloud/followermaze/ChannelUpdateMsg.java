/**
 * ChannelUpdateMsg.java  1.00 3.14.98 Michael Shoffner
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

public class ChannelUpdateMsg implements BusMsg {

  String name;
  Object message;
  
  public ChannelUpdateMsg (String n, Object m) {
    this.name = n;
    this.message = m;
  }

  public String getName () {
    return name;
  }

  public Object getMessage () {
    return message;
  }
  
}
