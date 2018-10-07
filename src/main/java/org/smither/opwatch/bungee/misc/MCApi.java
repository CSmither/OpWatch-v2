package org.smither.opwatch.bungee.misc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class MCApi {
  public static String getUsername(UUID uuid) {
    try {

      URL url =
          new URL(
              "https://api.mojang.com/user/profiles/"
                  + uuid.toString().replaceAll("-", "")
                  + "/names");
      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }

      try {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(sb.toString());
        JSONArray array = (JSONArray) obj;
        JSONObject curName = (JSONObject) array.get(0);
        return (String) curName.get("name");
      } catch (ParseException e) {
        e.printStackTrace();
      }

      conn.disconnect();

    } catch (MalformedURLException e) {

      e.printStackTrace();

    } catch (IOException e) {

      e.printStackTrace();
    }
    return null;
  }
}
