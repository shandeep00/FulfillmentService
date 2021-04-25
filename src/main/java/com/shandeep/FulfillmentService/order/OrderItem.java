package com.shandeep.FulfillmentService.order;

import com.shandeep.FulfillmentService.entity.Item;

import java.io.Serializable;

public class OrderItem
    implements Serializable
{
    private Item item;
    private int quantity;
    private double price;

    public OrderItem(Item item, int quantity, double price)
    {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }

    public Item getItem()
    {
        return item;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public double getPrice()
    {
        return price;
    }
}
