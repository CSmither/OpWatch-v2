package org.smither.opwatch.bungee.messaging.dto;

import org.smither.opwatch.bungee.misc.SignWipeResult;
import org.smither.opwatch.bungee.repos.docs.BungeeLocation;

import java.io.Serializable;

public class SignWipeReply implements Serializable {
  private int code;
  private BungeeLocation location;
  private SignWipeResult result;

  public SignWipeReply(int code, BungeeLocation location, SignWipeResult result) {
    this.code = code;
    this.location = location;
    this.result = result;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public BungeeLocation getLocation() {
    return location;
  }

  public void setLocation(BungeeLocation location) {
    this.location = location;
  }

  public SignWipeResult getResult() {
    return result;
  }

  public void setResult(SignWipeResult result) {
    this.result = result;
  }
}
