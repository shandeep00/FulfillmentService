package com.shandeep.FulfillmentService.order;

import java.io.Serializable;
import java.util.List;

public class OrderSummary
    implements Serializable
{
    private String customerName;
    private List<OrderItem> orderDetails;
    private double orderTotal;
    private String storeId;
    private String deliveryTime;

    public OrderSummary(String customerName, List<OrderItem> orderDetails, double orderTotal, String storeId, String deliveryTime)
    {
        this.customerName = customerName;
        this.orderDetails = orderDetails;
        this.orderTotal = orderTotal;
        this.storeId = storeId;
        this.deliveryTime = deliveryTime;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public List<OrderItem> getOrderDetails()
    {
        return orderDetails;
    }

    public double getOrderTotal()
    {
        return orderTotal;
    }

    public String getStoreId()
    {
        return storeId;
    }

    public String getDeliveryTime()
    {
        return deliveryTime;
    }
}
