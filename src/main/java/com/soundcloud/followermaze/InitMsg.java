/**
 * InitMsg.java  1.00 3.14.98 Michael Shoffner
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

import java.util.*;

public class InitMsg implements BusMsg {

  Vector channelNames;
  
  public InitMsg (Vector cn) {
    channelNames = cn;
  }

  public Vector getChannelNames () {
    return channelNames;
  }
}

  
