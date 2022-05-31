package com.example.wpamapp;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class SupplaApiRequest implements Callable<String> {
    private long from_time;
    private long to_time;

    SupplaApiRequest(long from_time, long to_time) {
        this.from_time = from_time;
        this.to_time = to_time;
    }

    public String call() {
        try {

            URL url = new URL("https://svr30.supla.org/api/v2.3.0/channels/5830/measurement-logs?limit=5000&offset=0");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Authorization", "Bearer NDVhNzYwYTc0ZDY0OTE2OWM0OTZiYWY5NWI5NjA0MWY2MGM5MjI4MGFmZDAzN2FhMDgxZmE4ODI4M2Y4ZGZmOA.aHR0cHM6Ly9zdnIzMC5zdXBsYS5vcmc=");
            con.addRequestProperty("accept", "application/json");
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            String json_data = content.toString();
            JSONArray arr = new JSONArray(json_data);
            Integer lastTimestamp = arr.getJSONObject(arr.length() -1).getInt("date_timestamp");
            Integer from = (int)(from_time / 1000);
            return json_data;
            //final JSONArray arr = new JSONArray(json_data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
