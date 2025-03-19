package org.retroclubkit.team;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.retroclubkit.exception.DomainException;
import org.retroclubkit.exception.TeamAlreadyExistException;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.repository.TeamRepository;
import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.web.dto.CreatedNewTeam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TeamServiceUTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private CreatedNewTeam createdNewTeam;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(UUID.randomUUID());
        team.setName("Retro FC");
        team.setCountry("Bulgaria");
        team.setDeleted(false);

        createdNewTeam = CreatedNewTeam.builder()
                .name(team.getName())
                .country(team.getCountry())
                .build();
    }

    @Test
    void getById_ShouldReturnTeam_WhenExists() {
        // Given
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        // When
        Team result = teamService.getById(team.getId());

        // Then
        assertNotNull(result);
        assertEquals(team.getId(), result.getId());
        verify(teamRepository, times(1)).findById(team.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // Given
        UUID teamId = UUID.randomUUID();
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Then
        assertThrows(DomainException.class, () -> teamService.getById(teamId));
    }

    @Test
    void getAllTeamsWhichIsNotDeleted_ShouldReturnTeams() {
        // Given
        when(teamRepository.findAllByIsDeletedFalse()).thenReturn(List.of(team));

        // When
        List<Team> result = teamService.getAllTeamsWhichIsNotDeleted();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(teamRepository, times(1)).findAllByIsDeletedFalse();
    }

    @Test
    void saveTeam_ShouldSaveNewTeam() {
        // Given
        when(teamRepository.findByName(createdNewTeam.getName())).thenReturn(Optional.empty());

        // When
        teamService.saveTeam(createdNewTeam);

        // Then
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void saveTeam_ShouldThrowException_WhenTeamAlreadyExists() {
        // Given
        when(teamRepository.findByName(createdNewTeam.getName())).thenReturn(Optional.of(team));

        // Then
        assertThrows(TeamAlreadyExistException.class, () -> teamService.saveTeam(createdNewTeam));
    }

    @Test
    void deleteTeamById_ShouldMarkTeamAndTshirtsAsDeleted() {
        // Given
        Tshirt tshirt = new Tshirt();
        tshirt.setDeleted(false);
        tshirt.setAvailable(true);
        team.setTshirts(List.of(tshirt));
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        // When
        teamService.deleteTeamById(team.getId());

        // Then
        assertTrue(team.isDeleted());
        assertTrue(tshirt.isDeleted());
        assertFalse(tshirt.isAvailable());
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    void deleteTeamById_ShouldThrowException_WhenTeamNotFound() {
        // Given
        UUID teamId = UUID.randomUUID();
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Then
        assertThrows(DomainException.class, () -> teamService.deleteTeamById(teamId));
    }
}

