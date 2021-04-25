package com.shandeep.FulfillmentService.entity;

public class PhysicalStore
{
    private String storeId;
    private String turnAroundTime;

    public PhysicalStore(String storeId, String turnAroundTime)
    {
        this.storeId = storeId;
        this.turnAroundTime = turnAroundTime;
    }

    public String getStoreId()
    {
        return storeId;
    }

    public String getTurnAroundTime()
    {
        return turnAroundTime;
    }
}
