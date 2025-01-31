package org.retroclubkit.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String name;
    private String country;
    private String address;
    private String city;
    private String phone;
    private String paymentMethod;
    private List<TshirtOrderRequest> cartItems; // Списък с продуктите в поръчката
}
