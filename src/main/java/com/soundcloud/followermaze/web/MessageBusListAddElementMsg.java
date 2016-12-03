
package com.soundcloud.followermaze.web;

public class MessageBusListAddElementMsg extends MessageBusListMsg {

  protected String sourceID;
  
  public MessageBusListAddElementMsg (String sid, String mid, Object m) {
    super (mid, m);
    this.sourceID = sid;
  }

  public String getSourceID () {
    return sourceID;
  }
  
}
