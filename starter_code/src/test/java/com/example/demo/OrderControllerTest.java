package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private final UserRepository users = mock(UserRepository.class);

    @Mock
    private final OrderRepository orders = mock(OrderRepository.class);

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void submitTest(){
        User user = saveUser();
        Item item = saveItem();

        Cart cart = user.getCart();

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);

        cart.setUser(user);
        user.setCart(cart);

        when(users.findByUsername("chuongdn79")).thenReturn(user);

        ResponseEntity<UserOrder> response =  orderController.submit("chuongdn79");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder retrievedUserOrder = response.getBody();
        assertNotNull(retrievedUserOrder);
        assertNotNull(retrievedUserOrder.getItems());
        assertNotNull(retrievedUserOrder.getTotal());
        assertNotNull(retrievedUserOrder.getUser());
    }

    @Test
    public void testSubmitNullUser() {
        User user = saveUser();
        Item item = saveItem();
        Cart cart = user.getCart();

        cart.setUser(user);
        user.setCart(cart);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);

        when(users.findByUsername("chuongdn79")).thenReturn(null);
        ResponseEntity<UserOrder> response =  orderController.submit("chuongdn79");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void ordersByUserTest(){
        User user = saveUser();
        Item item = saveItem();
        Cart cart = user.getCart();

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);

        cart.setUser(user);
        user.setCart(cart);

        when(users.findByUsername("chuongdn79")).thenReturn(user);
        orderController.submit("chuongdn79");

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("chuongdn79");

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<UserOrder> userOrders = responseEntity.getBody();
        assertNotNull(userOrders);
        assertEquals(0, userOrders.size());
    }

    @Test
    public void ordersByUserNullUser(){
        User user = saveUser();
        Item item = saveItem();
        Cart cart = user.getCart();

        cart.setUser(user);
        user.setCart(cart);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);


        when(users.findByUsername("chuongdn79")).thenReturn(null);

        orderController.submit("chuongdn79");

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("chuongdn79");

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    public static Item saveItem(){
        Item item = new Item();

        item.setId(1L);
        item.setName("Gucci");
        item.setDescription("Gucci gang.");
        item.setPrice(BigDecimal.valueOf(55.0));

        return item;
    }

    public static User saveUser(){
        User user = new User();

        user.setId(1);
        user.setUsername("chuongdn79");
        user.setPassword("chuong123");

        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setUser(null);
        emptyCart.setItems(new ArrayList<Item>());
        emptyCart.setTotal(BigDecimal.valueOf(0.0));
        user.setCart(emptyCart);

        return user;
    }

}