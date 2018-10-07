package org.smither.opwatch.bungee;

import org.smither.opwatch.bungee.messaging.BroadcastManager;
import org.smither.opwatch.bungee.messaging.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexChecker {

  private static final List<Pattern> rxs = new ArrayList<>();

  private static RegexChecker instance;

  private Opwatch opwatch;

  private BroadcastManager broadcastManager;

  private RegexChecker() {
    try {
      opwatch = Opwatch.getInstance();
      broadcastManager = BroadcastManager.getInstance();
      Scanner s =
          new Scanner(
              new File(opwatch.getDataFolder(), opwatch.getConfig().getString("RegexCheckFile")));
      while (s.hasNext()) {
        String rule = s.next();
        try {
          rxs.add(Pattern.compile(rule));
        } catch (PatternSyntaxException ex) {
          broadcastManager.sendMessage(
              new Message(
                  "OPWATCH ERROR!",
                  "Regex Pattern load failed on regex \"" + ex.getPattern() + "\""));
        }
      }
      opwatch.getLogger().info(Integer.toString(rxs.size()) + " patterns loaded");
      s.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static RegexChecker getInstance() {
    if (instance == null) {
      instance = new RegexChecker();
    }
    return instance;
  }

  public boolean check(final String str) {
    for (Pattern rx : rxs) {
      if (rx.matcher(str).find()) {
        return true;
      }
    }
    return false;
  }
}
