package com.shopping.service;

import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

import com.shopping.stubs.order.Order;
import com.shopping.client.OrderClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    UserDao userDao = new UserDao();
    
    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        
        /* 
        * Getting user domain object
        * Calling DB layer to get the user details based on field of the gRCP message
        */
        User user = userDao.getDetails(request.getUsername());

        /* 
        * We have Builder Design Pattern -that gRCP uses- to create proto objects
        * corresponding to the message types in our proto file
        */

        // Transforming DB layer properties to our response proto object
        UserResponse.Builder userResponseBuilder =
                UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()));

        List<Order> orders = getOrders(userResponseBuilder);

        userResponseBuilder.setNoOfOrders(orders.size());        

        UserResponse userResponse = userResponseBuilder.build();
        responseObserver.onNext(userResponse); // To return that user response back to the client
        responseObserver.onCompleted(); // To ensure that the RPC call gets completed

    }


    private List<Order> getOrders(UserResponse.Builder userResponseBuilder) {
        //get orders by invoking the Order Client
        logger.info("Creating a channel and calling the Order Client");
        // Channel creation
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                                .usePlaintext().build();
        // Fedding channel to the client, which will get you the stub                        
        OrderClient orderClient = new OrderClient(channel);
        // The client call the service by using that stub
        List<Order> orders = orderClient.getOrders(userResponseBuilder.getId());

        // Close the channel after 5 seconds
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.SEVERE, "Channel did not shutdown", exception);
        }
        return orders;
    }
}
