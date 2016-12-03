package com.soundcloud.followermaze.web;

import java.io.*;

public abstract class MessageBusListMsg implements Serializable {

  protected String messageID;
  protected Object message;
  
  public MessageBusListMsg (String messageID, Object message) {
    this.messageID = messageID;
    this.message = message;
  }

  public String getMessageID () {
    return messageID;
  }

  public Object getMessage () {
    return message;
  }
}
