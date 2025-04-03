package org.retroclubkit.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.retroclubkit.tshirt.model.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateTshirtRequest {

    @NotNull(message = "ID cannot be null.")
    private UUID id;

    @NotBlank(message = "T-shirt name cannot be empty.")
    private String name;

    @NotBlank(message = "Image URL cannot be empty.")
    private String image;

    @NotEmpty(message = "At least one size must be selected.")
    private List<Size> sizes;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01.")
    private BigDecimal price;

}
