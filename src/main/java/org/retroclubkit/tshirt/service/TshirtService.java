package org.retroclubkit.tshirt.service;

import jakarta.transaction.Transactional;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.retroclubkit.web.dto.TshirtAdminRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
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
    public List<TshirtAdminRequest> getAvailableTshirts() {
        return tshirtRepository.findByIsAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Филтрира само неналичните тениски
    public List<TshirtAdminRequest> getUnavailableTshirts() {
        return tshirtRepository.findByIsAvailableFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Търси по категория
    public List<TshirtAdminRequest> getTshirtsByCategory(Category category) {
        return tshirtRepository.findByCategory(category)
                .stream()
                .map(tshirt -> convertToDTO((Tshirt) tshirt)) // ✅ Изрично указваме типа
                .collect(Collectors.toList());
    }

    // Запазване / редактиране на тениска
    public void saveTshirt(TshirtAdminRequest tshirtAdminRequest) {
        Tshirt tshirt = convertToEntity(tshirtAdminRequest);
        tshirtRepository.save(tshirt);
    }

    // Изтриване на тениска
    public void deleteTshirt(UUID id) {
        tshirtRepository.deleteById(id);
    }

    // Превръща модел в DTO
    public TshirtAdminRequest convertToDTO(Tshirt tshirt) {
        return new TshirtAdminRequest(
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
    public Tshirt convertToEntity(TshirtAdminRequest dto) {

        Team team = teamService.getById(dto.getTeamId());

        return new Tshirt(
                dto.getId() != null ? dto.getId() : UUID.randomUUID(),
                dto.getName(),
                dto.getPrice(),
                dto.getImage(),
                dto.getCategory(),
                dto.getSizes(),
                dto.isAvailable(),
                team,
                null
        );
    }

    public List<Tshirt> getTshirtsByCategoryAndAvailable(Category category) {
        return tshirtRepository.getTshirtsByCategoryAndIsAvailableTrue(category);
    }

    public List<Tshirt> getAllTshirtsLimitAndAvailableTrue() {
        return tshirtRepository.getAllTshirtsLimitAvailableTrue(10);
    }

    public List<Tshirt> getAllTshirts() {
        return tshirtRepository.findAll();
    }

    public Tshirt getById(UUID id) {
        return tshirtRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateTshirtBySizeAndPrice(UUID id, List<Size> newSizes, BigDecimal newPrice) {
        Tshirt tshirt = tshirtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tshirt not found"));

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
        return tshirtRepository.findByTeamNameIgnoreCase(teamName);
    }

    public boolean existsByName(String name) {
        return tshirtRepository.existsByNameIgnoreCase(name);
    }
}
