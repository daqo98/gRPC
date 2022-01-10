package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
    private OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderDao.getOrders(request.getUserId());
        logger.info("Got orders from OrderDao and converting to OrderResponse proto objects");
        /* 
        * Transform orders domain object into orders proto object
        * Before, using a streamig API from Java to transform every order dom obj to order proto object
        * And then make a list of those proto objects
        */
        List<com.shopping.stubs.order.Order> ordersForUser =  orders.stream().map(order -> com.shopping.stubs.order.Order.newBuilder()
        .setUserId(order.getUserId())
        .setOrderId(order.getOrderId())
        .setNoOfItems(order.getNoOfItems())
        .setTotalAmount(order.getTotalAmount())
        .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime())).build())
                .collect(Collectors.toList());

        // Bundle prior list in the orderResponse object        
        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(ordersForUser).build();
        responseObserver.onNext(orderResponse); // To send the orderResponse object to the client
        responseObserver.onCompleted(); // Ensure successful completion of RPC call

    }
}
