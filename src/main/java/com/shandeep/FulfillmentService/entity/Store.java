package com.shandeep.FulfillmentService.entity;

import java.util.HashMap;
import java.util.Map;

public class Store
{
    private String id;
    private double latitude;
    private double longitude;
    private int parcelTime;
    private Map<String, Integer> inventory;

    public Store(String id, double latitude, double longitude, int parcelTime)
    {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parcelTime = parcelTime;
        inventory = new HashMap<>();
    }

    public String getId()
    {
        return id;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public int getParcelTime()
    {
        return parcelTime;
    }

    public Map<String, Integer> getInventory()
    {
        return inventory;
    }

    public void setInventory(Map<String, Integer> inventory)
    {
        this.inventory = inventory;
    }
}
