package com.shandeep.FulfillmentService.cart;

public class CartItem
{
    private String itemId;
    private int quantity;

    public CartItem(String itemId, int quantity)
    {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId()
    {
        return itemId;
    }

    public int getQuantity()
    {
        return quantity;
    }
}
