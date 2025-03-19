package org.retroclubkit.tshirt.service;

import jakarta.transaction.Transactional;
import org.retroclubkit.exception.DomainException;
import org.retroclubkit.exception.MustBePositiveException;
import org.retroclubkit.exception.TshirtAlreadyExistException;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.retroclubkit.web.dto.CreatedNewTshirt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TshirtService {

    private final TshirtRepository tshirtRepository;
    private final TeamService teamService;

    @Autowired
    public TshirtService(TshirtRepository tshirtRepository, TeamService teamService) {
        this.tshirtRepository = tshirtRepository;
        this.teamService = teamService;
    }

    // Филтрира само наличните тениски
    public List<CreatedNewTshirt> getAvailableTshirts() {
        return tshirtRepository.findByIsAvailableTrueAndDeletedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Филтрира само неналичните тениски
    public List<CreatedNewTshirt> getUnavailableTshirts() {
        return tshirtRepository.findByIsAvailableFalseAndDeletedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Запазване / редактиране на тениска
    public void saveTshirt(CreatedNewTshirt createdNewTshirt) {
        Tshirt tshirt = convertToEntity(createdNewTshirt);
        tshirtRepository.save(tshirt);
    }

    public void deleteTshirtById(UUID id) {
        Tshirt tshirt = tshirtRepository.findById(id).orElseThrow(() -> new DomainException("T-shirt with id [%s] does not exist.".formatted(id)));
        tshirt.setDeleted(true);
        tshirtRepository.save(tshirt);
    }

    // Превръща модел в DTO
    public CreatedNewTshirt convertToDTO(Tshirt tshirt) {
        return new CreatedNewTshirt(
                tshirt.getId(),
                tshirt.getName(),
                tshirt.getPrice(),
                tshirt.getImage(),
                tshirt.getCategory(),
                tshirt.getSizes(),
                tshirt.isAvailable(),
                tshirt.getTeam().getId()
        );
    }

    // Превръща DTO в модел
    public Tshirt convertToEntity(CreatedNewTshirt dto) {

        Team team = teamService.getById(dto.getTeamId());
        Tshirt tshirt = tshirtRepository.findByNameAndDeletedFalse(dto.getName()).orElse(null);
        if(tshirt!=null) {
            throw new TshirtAlreadyExistException("T-shirt with name [%s] already exist.".formatted(dto.getName()));
        }

        return Tshirt.builder().
                name(dto.getName()).
                price(dto.getPrice()).
                image(dto.getImage()).
                category(dto.getCategory()).
                sizes(dto.getSizes()).
                isAvailable(dto.isAvailable()).
                team(team).order(null).
                build();


    }

    public List<Tshirt> getTshirtsByCategoryAndAvailable(Category category) {
        return tshirtRepository.getTshirtsByCategoryAndIsAvailableTrueAndDeletedFalse(category);
    }

    public List<Tshirt> getAllTshirtsLimitAndAvailableTrue() {
        return tshirtRepository.getAllTshirtsLimitAvailableTrueAndDeletedFalse(20);
    }

    public Tshirt getById(UUID id) {
        return tshirtRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateTshirtBySizeAndPrice(UUID id, String name, String image, List<Size> newSizes, BigDecimal newPrice) {
        Tshirt tshirt = tshirtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tshirt not found"));

        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new MustBePositiveException("The price cannot be negative.");
        }

        tshirt.setName(name);
        tshirt.setImage(image);
        tshirt.setSizes(newSizes);
        tshirt.setPrice(newPrice);
        tshirtRepository.save(tshirt);
    }

    @Transactional
    public void toggleAvailability(UUID id) {
        Tshirt tshirt = tshirtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tshirt not found"));

        tshirt.setAvailable(!tshirt.isAvailable()); // ✅ Смяна на наличността
        tshirtRepository.save(tshirt);
    }

    public List<Tshirt> findTshirtsByTeam(String teamName) {
        return tshirtRepository.findByTeamNameIgnoreCaseAndDeletedFalse(teamName);
    }

    public List<Tshirt> getAllTshirtsWhichIsNotDeleted() {
        return tshirtRepository.getAllTshirtsDeletedFalse();
    }
}
