package com.shandeep.FulfillmentService.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServiceUtil
{
    public static boolean isAllItemsExist(Map<String, Integer> itemQuantity, Map<String, Integer> inventory)
    {
        for (Map.Entry<String, Integer> item : itemQuantity.entrySet()) {
            String itemId = item.getKey();
            int quantity = item.getValue();
            if (!inventory.containsKey(itemId) || inventory.get(itemId) < quantity) {
                return false;
            }
        }
        return true;
    }

    public static long getEstimatedTime(double originLatitude, double originLongitude, double destLatitude, double destLongitude, double speed)
    {
        originLatitude = Math.toRadians(originLatitude);
        destLatitude = Math.toRadians(destLatitude);
        originLongitude = Math.toRadians(originLongitude);
        destLongitude = Math.toRadians(destLongitude);

        // Haversine formula
        double dlat = destLatitude - originLatitude;
        double dlon = destLongitude - originLongitude;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(originLatitude) * Math.cos(destLatitude)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double distance = 6371 * c;
        speed /= 60;
        double time = distance/speed;
        return (long) time;
    }

    public static long getEstimatedTimeUsingMaps(double originLatitude, double originLongitude, double destLatitude, double destLongitude, String apiKey)
    {
        String url = "https://api.distancematrix.ai/maps/api/distancematrix/json" +
                "?origins=" + originLatitude + "," + originLongitude +
                "&destinations=" + destLatitude + "," + destLongitude +
                "&key=" + apiKey;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return findDrivingTime(response);
        }
        catch (IOException e) {
            System.out.println("Failed to calculate turn around time(TAT)");
            e.printStackTrace();
        }
        return -1;
    }

    private static long findDrivingTime(Response response)
    {
        try {
            JSONParser parser = new JSONParser();

            Object obj = parser.parse(response.body().string());
            JSONObject jsonobj = (JSONObject) obj;

            JSONArray rows = (JSONArray) jsonobj.get("rows");
            JSONObject objects = (JSONObject) rows.get(0);
            JSONArray elements = (JSONArray) objects.get("elements");
            JSONObject parameters = (JSONObject) elements.get(0);
            JSONObject duration = (JSONObject) parameters.get("duration");
            long estimatedTime = (long) duration.get("value");
            return estimatedTime;
        }
        catch (IOException | ParseException e) {
            System.out.println("Unable to calculate driving time from distancematrix response");
            e.printStackTrace();
        }
        return -1;
    }

    public static Map<String, Integer> getUpdatedInventory(Map<String, Integer> inventory, Map<String, Integer> itemQuantity)
    {
        Map<String, Integer> updatedInventory = new HashMap<>(itemQuantity.size());
        for (Map.Entry<String, Integer> item : itemQuantity.entrySet()) {
            String itemId = item.getKey();
            updatedInventory.put(itemId, inventory.get(itemId) - item.getValue());
        }
        return updatedInventory;
    }

    public static String convertTime(long time)
    {
        long hours = time/3600;
        time %= 3600;
        long minutes = time/60;
        return hours + " hours " + minutes + " minutes";
    }
}
