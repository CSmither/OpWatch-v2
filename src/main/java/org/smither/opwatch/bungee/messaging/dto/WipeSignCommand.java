package org.smither.opwatch.bungee.messaging.dto;

import org.smither.opwatch.bungee.repos.docs.SignChange;

import java.io.Serializable;

public class WipeSignCommand implements Serializable {
  SignChange sc;
  String[] newContent;

  public WipeSignCommand(SignChange sc, String[] newContent) {
    this.sc = sc;
    this.newContent = newContent;
  }

  public SignChange getSc() {
    return sc;
  }

  public void setSc(SignChange sc) {
    this.sc = sc;
  }

  public String[] getNewContent() {
    return newContent;
  }

  public void setNewContent(String[] newContent) {
    this.newContent = newContent;
  }
}
