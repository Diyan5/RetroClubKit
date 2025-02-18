package org.retroclubkit.web.dto;

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
    @NotNull
    private String name;
    @NotNull
    @Positive(message = "Must be positive")
    private BigDecimal price;
    @NotNull
    private String image;
    @NotNull
    private Category category;
    @NotNull
    private List<Size> sizes;
    private boolean isAvailable;
    private UUID teamId;


}
