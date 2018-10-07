package org.smither.opwatch.bungee.messaging;

import java.util.HashMap;

public class BroadcastManager {
  private static BroadcastManager instance;
  private HashMap<String, Broadcaster> broadcasters;

  private BroadcastManager() {
    this.broadcasters = new HashMap<>();
  }

  public static BroadcastManager getInstance() {
    if (instance == null) {
      instance = new BroadcastManager();
    }
    return instance;
  }

  public boolean register(String name, Broadcaster broadcaster) {
    Broadcaster broadcaster1 = broadcasters.putIfAbsent(name, broadcaster);
    return broadcaster == broadcaster;
  }

  public void unRegister(String name) {
    if (!broadcasters.containsKey(name)) {
      return;
    } else {
      broadcasters.remove(name);
    }
  }

  public void sendMessage(Message message) {
    for (Broadcaster broadcaster : broadcasters.values()) {
      broadcaster.broadcast(message);
    }
  }

  public void sendMessage(String broadcaster, Message message) {
    broadcasters.get(broadcaster).broadcast(message);
  }
}
