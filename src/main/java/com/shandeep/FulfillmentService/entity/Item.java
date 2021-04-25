package com.shandeep.FulfillmentService.entity;

import java.io.Serializable;

public class Item
    implements Serializable
{
    private String category;
    private String id;
    private String name;
    private double mrp;

    public Item(String category, String id, String name, double mrp)
    {
        this.id = id;
        this.category = category;
        this.name = name;
        this.mrp = mrp;
    }

    public String getCategory()
    {
        return category;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public double getMrp()
    {
        return mrp;
    }
}
