package com.shandeep.FulfillmentService.database;

import com.shandeep.FulfillmentService.entity.Item;
import com.shandeep.FulfillmentService.entity.Store;
import com.shandeep.FulfillmentService.order.OrderSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.shandeep.FulfillmentService.database.DBUtil.deserializeObject;
import static com.shandeep.FulfillmentService.database.DBUtil.getConnection;
import static com.shandeep.FulfillmentService.database.DBUtil.getResultSet;
import static com.shandeep.FulfillmentService.database.DBUtil.serializeObject;

public class DBConnection
{
    private static Connection connection = null;

    public static Map<String, Store> getStoresInfoFromDB(Map<String, Integer> itemQuantity)
    {
        Map<String, Store> stores = new HashMap<>();
        try {
            if (connection == null) {
                connection = getConnection();
            }

            ResultSet rs1 = getResultSet(connection, "SELECT * FROM stores");
            while (rs1.next()) {
                String id = rs1.getString("storeid");
                double latitude = rs1.getDouble("latitude");
                double longitude = rs1.getDouble("longitude");
                int parcelTime = rs1.getInt("parceltime");
                stores.put(id, new Store(id, latitude, longitude, parcelTime));
            }

            String sql = "SELECT * FROM inventory WHERE itemid IN (";
            for (String itemid : itemQuantity.keySet()) {
                sql += "'" + itemid + "',";
            }
            String query = sql.substring(0, sql.length() - 1) + ")";
            ResultSet rs2 = getResultSet(connection, query);
            while (rs2.next()) {
                String storeId = rs2.getString("storeid");
                String itemId = rs2.getString("itemid");
                int quantity = rs2.getInt("quantity");
                Store store = stores.get(storeId);
                Map<String, Integer> inventory = store.getInventory();
                inventory.put(itemId, inventory.getOrDefault(itemId, 0) + quantity);
                store.setInventory(inventory);
                stores.put(storeId, store);
            }
            return stores;
        }
        catch (SQLException e) {
            System.out.println("Unable to fetch store details");
            e.printStackTrace();
        }
        return stores;
    }

    public static Map<String, Item> getItemDetailsFromDB(Map<String, Integer> itemQuantity)
    {
        Map<String, Item> productDetails = new HashMap<>(itemQuantity.size());
        try {
            if (connection == null) {
                connection = getConnection();
            }

            String sql = "SELECT * FROM items WHERE itemid IN (";
            for (String itemid : itemQuantity.keySet()) {
                sql += "'" + itemid + "',";
            }
            String query = sql.substring(0, sql.length() - 1) + ")";
            ResultSet rs1 = getResultSet(connection, query);
            while (rs1.next()) {
                String category = rs1.getString("category");
                String itemId = rs1.getString("itemid");
                String name = rs1.getString("itemname");
                double mrp = rs1.getDouble("mrp");
                productDetails.put(itemId, new Item(category, itemId, name, mrp));
            }
            return productDetails;
        }
        catch (SQLException e) {
            System.out.println("Unable to fetch item details");
            e.printStackTrace();
        }
        return productDetails;
    }

    public static void updateStoreInventoryInDB(String storeId, Map<String, Integer> updatedInventory) {
        try {
            if (connection == null) {
                connection = getConnection();
            }

            String sql = "UPDATE inventory " +
                    "SET quantity = CASE itemid";

            for (Map.Entry<String, Integer> item : updatedInventory.entrySet()) {
                sql += " WHEN '" + item.getKey() + "' THEN " + item.getValue();
            }
            sql += " ELSE quantity END " +
                    "WHERE storeid = '" + storeId + "'";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Inventory update failed");
            e.printStackTrace();
        }
    }

    public static void createOrderinDB(OrderSummary orderSummary)
    {
        try {
            if (connection == null) {
                connection = getConnection();
            }

            String sql = "INSERT INTO orders(ordersummary) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBytes(1, serializeObject(orderSummary));
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("creating order in database failed");
            e.printStackTrace();
        }
    }

    public static OrderSummary getOrderFromDB(long orderId)
    {
        try {
            if (connection == null) {
                connection = getConnection();
            }

            String sql = "SELECT ordersummary FROM orders WHERE orderid = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, orderId);
            ResultSet rs = statement.executeQuery();
            rs.next();
            byte[] buffer = rs.getBytes(1);
            return deserializeObject(buffer);
        }
        catch (SQLException e) {
            System.out.println("Failed to get order details");
            e.printStackTrace();
        }
        return null;
    }
}
