package org.smither.opwatch.bungee.messaging;

import java.util.Date;

public class Message {
  private String header;
  private String content;
  private Date timestamp;

  public Message(String header, String content) {
    this.header = header;
    this.content = content;
    this.timestamp = new Date();
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
