package org.retroclubkit.tshirt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.retroclubkit.exception.DomainException;
import org.retroclubkit.exception.MustBePositiveException;
import org.retroclubkit.exception.TshirtAlreadyExistException;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.web.dto.CreatedNewTshirt;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TshirtServiceUTest {

    @Mock
    private TshirtRepository tshirtRepository;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TshirtService tshirtService;

    private Tshirt tshirt;
    private CreatedNewTshirt createdNewTshirt;
    private Team team;
    private UUID tshirtId;
    private String newName;
    private String newImage;
    private List<Size> newSizes;
    private BigDecimal newPrice;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(UUID.randomUUID());

        tshirtId = UUID.randomUUID();
        newName = "Updated Retro Tee";
        newImage = "new_image_url";
        newSizes = List.of(Size.S, Size.M, Size.L);
        newPrice = BigDecimal.valueOf(34.99);

        tshirt = Tshirt.builder()
                .id(tshirtId)
                .name("Retro Tee")
                .price(BigDecimal.valueOf(29.99))
                .image("image_url")
                .category(Category.NEW)
                .sizes(List.of(Size.M, Size.L))
                .isAvailable(true)
                .team(team)
                .build();

        createdNewTshirt = new CreatedNewTshirt(
                tshirt.getId(),
                tshirt.getName(),
                tshirt.getPrice(),
                tshirt.getImage(),
                tshirt.getCategory(),
                tshirt.getSizes(),
                tshirt.isAvailable(),
                team.getId()
        );
    }

    @Test
    void updateTshirtBySizeAndPrice_ShouldUpdateTshirt_WhenValidDataProvided() {
        // Given
        when(tshirtRepository.findById(tshirtId)).thenReturn(Optional.of(tshirt));

        // When
        tshirtService.updateTshirtBySizeAndPrice(tshirtId, newName, newImage, newSizes, newPrice);

        // Then
        assertEquals(newName, tshirt.getName());
        assertEquals(newImage, tshirt.getImage());
        assertEquals(newSizes, tshirt.getSizes());
        assertEquals(newPrice, tshirt.getPrice());
        verify(tshirtRepository, times(1)).save(tshirt);
    }

    @Test
    void updateTshirtBySizeAndPrice_ShouldThrowException_WhenTshirtNotFound() {
        // Given
        when(tshirtRepository.findById(tshirtId)).thenReturn(Optional.empty());

        // Then
        assertThrows(RuntimeException.class, () ->
                tshirtService.updateTshirtBySizeAndPrice(tshirtId, newName, newImage, newSizes, newPrice));

        verify(tshirtRepository, never()).save(any(Tshirt.class));
    }

    @Test
    void updateTshirtBySizeAndPrice_ShouldThrowMustBePositiveException_WhenPriceIsNegative() {
        // Given
        when(tshirtRepository.findById(tshirtId)).thenReturn(Optional.of(tshirt));

        // Then
        assertThrows(MustBePositiveException.class, () ->
                tshirtService.updateTshirtBySizeAndPrice(tshirtId, newName, newImage, newSizes, BigDecimal.valueOf(-10)));

        verify(tshirtRepository, never()).save(any(Tshirt.class));
    }

    @Test
    void toggleAvailability_ShouldToggleAvailability() {
        UUID tshirtId = tshirt.getId();
        when(tshirtRepository.findById(tshirtId)).thenReturn(Optional.of(tshirt));
        boolean initialAvailability = tshirt.isAvailable();

        tshirtService.toggleAvailability(tshirtId);

        assertEquals(!initialAvailability, tshirt.isAvailable());
        verify(tshirtRepository, times(1)).save(tshirt);
    }

    @Test
    void toggleAvailability_ShouldThrowException_WhenTshirtNotFound() {
        UUID tshirtId = UUID.randomUUID();
        when(tshirtRepository.findById(tshirtId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tshirtService.toggleAvailability(tshirtId));
    }

    @Test
    void findTshirtsByTeam_ShouldReturnTshirts() {
        String teamName = "Retro Team";
        when(tshirtRepository.findByTeamNameIgnoreCaseAndDeletedFalse(teamName)).thenReturn(List.of(tshirt));

        List<Tshirt> result = tshirtService.findTshirtsByTeam(teamName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tshirtRepository, times(1)).findByTeamNameIgnoreCaseAndDeletedFalse(teamName);
    }

    @Test
    void saveTshirt_ShouldSaveTshirt() {
        when(teamService.getById(team.getId())).thenReturn(team);
        when(tshirtRepository.findByNameAndDeletedFalse(createdNewTshirt.getName())).thenReturn(Optional.empty());

        tshirtService.saveTshirt(createdNewTshirt);

        verify(tshirtRepository, times(1)).save(any(Tshirt.class));
    }

    @Test
    void saveTshirt_ShouldThrowException_WhenTshirtAlreadyExists() {
        when(tshirtRepository.findByNameAndDeletedFalse(createdNewTshirt.getName())).thenReturn(Optional.of(tshirt));

        assertThrows(TshirtAlreadyExistException.class, () -> tshirtService.saveTshirt(createdNewTshirt));
    }

    @Test
    void deleteTshirtById_ShouldMarkTshirtAsDeleted() {
        when(tshirtRepository.findById(tshirt.getId())).thenReturn(Optional.of(tshirt));

        tshirtService.deleteTshirtById(tshirt.getId());

        assertTrue(tshirt.isDeleted());
        verify(tshirtRepository, times(1)).save(tshirt);
    }

    @Test
    void deleteTshirtById_ShouldThrowException_WhenTshirtNotFound() {
        UUID tshirtId = UUID.randomUUID();
        when(tshirtRepository.findById(tshirtId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> tshirtService.deleteTshirtById(tshirtId));
    }

}
