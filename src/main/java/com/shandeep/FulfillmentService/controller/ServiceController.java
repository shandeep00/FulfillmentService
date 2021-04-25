package com.shandeep.FulfillmentService.controller;

import com.shandeep.FulfillmentService.cart.CartItem;
import com.shandeep.FulfillmentService.cart.Customer;
import com.shandeep.FulfillmentService.entity.Item;
import com.shandeep.FulfillmentService.entity.PhysicalStore;
import com.shandeep.FulfillmentService.entity.Store;
import com.shandeep.FulfillmentService.order.OrderItem;
import com.shandeep.FulfillmentService.order.OrderSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shandeep.FulfillmentService.controller.ServiceUtil.convertTime;
import static com.shandeep.FulfillmentService.controller.ServiceUtil.getEstimatedTime;
import static com.shandeep.FulfillmentService.controller.ServiceUtil.getUpdatedInventory;
import static com.shandeep.FulfillmentService.controller.ServiceUtil.isAllItemsExist;
import static com.shandeep.FulfillmentService.database.DBConnection.createOrderinDB;
import static com.shandeep.FulfillmentService.database.DBConnection.getItemDetailsFromDB;
import static com.shandeep.FulfillmentService.database.DBConnection.getOrderFromDB;
import static com.shandeep.FulfillmentService.database.DBConnection.getStoresInfoFromDB;
import static com.shandeep.FulfillmentService.database.DBConnection.updateStoreInventoryInDB;

@RestController
@RequestMapping("/service")
public class ServiceController
{
    @PostMapping("/createorder")
    public OrderSummary createOrder(@RequestBody Customer customer, @RequestParam("speed") double speed)
            throws Exception
    {
        String customerName = customer.getName();
        double latitude = customer.getLatitude();
        double longitude = customer.getLongitude();
        List<CartItem> cartItems = customer.getCartItems();
        Map<String, Integer> itemQuantity = new HashMap<>(cartItems.size());
        for (CartItem cartItem : cartItems) {
            String itemId = cartItem.getItemId();
            int quantity = cartItem.getQuantity();
            itemQuantity.put(itemId, itemQuantity.getOrDefault(itemId, 0) + quantity);
        }

        Map<String, Store> stores = getStoresInfoFromDB(itemQuantity);
        PhysicalStore physicalStore = findStoreForOrder(latitude, longitude, stores, itemQuantity, speed);

        Map<String, Item> productDetails = getItemDetailsFromDB(itemQuantity);

        List<OrderItem> orderItems = new ArrayList<>();
        double orderTotal = 0;
        for (Map.Entry<String, Item> product : productDetails.entrySet()) {
            String itemId = product.getKey();
            Item item = product.getValue();
            int quantity = itemQuantity.get(itemId);
            double price = quantity * item.getMrp();
            orderItems.add(new OrderItem(item, quantity, price));
            orderTotal += price;
        }
        System.out.println("Delivery time : " + physicalStore.getTurnAroundTime());
        OrderSummary orderSummary = new OrderSummary(customerName,
                                                    orderItems,
                                                    orderTotal,
                                                    physicalStore.getStoreId(),
                                                    physicalStore.getTurnAroundTime());
        createOrderinDB(orderSummary);
        return orderSummary;
    }

    @GetMapping("/getorder")
    public OrderSummary getOrder(@RequestParam("orderid") long orderId)
    {
        return getOrderFromDB(orderId);
    }

    private PhysicalStore findStoreForOrder(double originLatitude, double originLongitude, Map<String, Store> stores, Map<String, Integer> itemQuantity, double speed)
            throws Exception
    {
        long minTime = Long.MAX_VALUE;
        Store deliveryStore = null;
        for (Map.Entry<String, Store> entry : stores.entrySet()) {
            Store store = entry.getValue();
            if (isAllItemsExist(itemQuantity, store.getInventory())) {
                long turnAroundTime = getEstimatedTime(store.getLatitude(), store.getLongitude(), originLatitude, originLongitude, speed) + 3600 * store.getParcelTime();
                if (turnAroundTime < minTime) {
                    minTime = turnAroundTime;
                    deliveryStore = store;
                }
            }
        }

        if (deliveryStore == null) {
            throw new Exception("Item out of stock in all stores");
        }

        Map<String, Integer> updatedInventory = getUpdatedInventory(deliveryStore.getInventory(), itemQuantity);
        String deliveryStoreId = deliveryStore.getId();
        updateStoreInventoryInDB(deliveryStoreId, updatedInventory);
        return new PhysicalStore(deliveryStoreId, convertTime(minTime));
    }
}
