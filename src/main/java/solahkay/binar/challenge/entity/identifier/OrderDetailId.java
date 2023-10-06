package solahkay.binar.challenge.entity.identifier;

import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.Product;

import java.io.Serializable;

public class OrderDetailId implements Serializable {

    private Order order;

    private Product product;

}
