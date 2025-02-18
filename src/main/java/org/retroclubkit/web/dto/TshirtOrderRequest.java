package org.retroclubkit.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TshirtOrderRequest {
    private UUID id;
    private int quantity;
    private String size;
}
