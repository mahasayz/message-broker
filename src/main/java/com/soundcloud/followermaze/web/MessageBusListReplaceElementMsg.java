package com.soundcloud.followermaze.web;

public class MessageBusListReplaceElementMsg extends MessageBusListMsg {

  protected String newMessageID;
  
  public MessageBusListReplaceElementMsg (String oldID, String newID, Object newMess) {
    super (oldID, newMess);
    this.newMessageID = newID;
  }

  public String getNewMessageID () {
    return newMessageID;
  }
  
}
