package com.shandeep.FulfillmentService.cart;

import java.util.List;

public class Customer
{
    private String name;
    private double latitude;
    private double longitude;
    private List<CartItem> cartItems;

    public Customer(String name, double latitude, double longitude, List<CartItem> cartItems)
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cartItems = cartItems;
    }

    public String getName()
    {
        return name;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public List<CartItem> getCartItems()
    {
        return cartItems;
    }
}
