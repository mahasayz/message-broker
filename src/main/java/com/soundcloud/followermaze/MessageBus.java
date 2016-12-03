/**
 * MessageBus.java  1.00 3.14.98 Michael Shoffner
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

public interface MessageBus {
  public void initBroker() throws IOException;
  public String getBrokerName();
  public void addChannel(String name) throws IOException;
  public void publishChannelMessage(String name, Object message) throws IOException;
}
