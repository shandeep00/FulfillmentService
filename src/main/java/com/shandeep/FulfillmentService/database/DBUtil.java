package com.shandeep.FulfillmentService.database;

import com.shandeep.FulfillmentService.order.OrderSummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil
{
    public static Connection getConnection()
    {
        try {
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
            return DriverManager.getConnection(dbUrl, username, password);
        }
        catch (SQLException | URISyntaxException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet getResultSet(Connection connection, String query)
    {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Query execution failed");
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] serializeObject(OrderSummary orderSummary)
    {
        byte[] byteArrayObject = null;
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(orderSummary);

            oos.close();
            bos.close();
            byteArrayObject = bos.toByteArray();
        }
        catch (Exception e) {
            System.out.println("Failed to serialize ordersummary object");
            e.printStackTrace();
        }
        return byteArrayObject;
    }

    public static OrderSummary deserializeObject(byte[] buffer)
    {
        try {
            ObjectInputStream objectInputStream = null;
            if (buffer != null) {
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
            }
            OrderSummary orderSummary = (OrderSummary) objectInputStream.readObject();
            return orderSummary;
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to deserialize ordersummary object");
            e.printStackTrace();
        }
        return null;
    }
}
