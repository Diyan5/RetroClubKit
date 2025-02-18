package org.retroclubkit.web.dto;


import lombok.Data;

@Data
public class CartDataRequest {
    private String name;
    private String country;
    private String address;
    private String city;
    private String phone;
}
