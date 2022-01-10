package com.shopping.client;

import com.shopping.stubs.order.Order;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.Channel;

import java.util.List;
import java.util.logging.Logger;

public class OrderClient {
    private Logger logger = Logger.getLogger(OrderClient.class.getName());
    // Set up the HTTP/2 connection to the server (open a channel)
    // get a stub object
    // call service method

    // An instance variable for the stub
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    // Constructor - Instantiating the stub by using the service class
    public OrderClient(Channel channel){
        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
        }

    // Method to call the order service. Order in List<Order> is a stub object
    public List<Order> getOrders(int userId){
        logger.info("OrderClient calling the OrderService method");
        // Building request proto object
        OrderRequest orderRequest = OrderRequest.newBuilder().setUserId(userId).build();
        // Making a call to that service method 
        OrderResponse orderResponse =  orderServiceBlockingStub.getOrdersForUser(orderRequest);
        return orderResponse.getOrderList();
    }
}
