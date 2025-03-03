package org.retroclubkit.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatedNewTshirt {
    private UUID id;
    @NotBlank(message = "Name cannot be empty")
    @NotNull(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Price cannot be empty")
    @NotNull(message = "Price cannot be empty")
    @Positive(message = "Must be positive")
    private BigDecimal price;

    @NotBlank(message = "Image cannot be empty")
    @NotNull(message = "Image cannot be empty")
    private String image;

    @NotBlank(message = "Category cannot be empty")
    @NotNull(message = "Category cannot be empty")
    private Category category;

    @NotBlank(message = "Size cannot be empty")
    @NotNull(message = "Size cannot be empty")
    private List<Size> sizes;

    private boolean isAvailable;

    private UUID teamId;


}
