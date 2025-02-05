package org.retroclubkit.web.dto;

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
public class TshirtAdminRequest {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String image;
    private Category category;
    private List<Size> sizes;
    private boolean isAvailable;
    private UUID teamId;


}
