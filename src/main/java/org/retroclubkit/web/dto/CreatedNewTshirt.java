package org.retroclubkit.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
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

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Image URL cannot be empty")
    @URL(message = "Requires correct web link format")
    private String image;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Size is required")
    private List<Size> sizes;

    private boolean isAvailable;

    private UUID teamId;


}
