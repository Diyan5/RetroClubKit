package org.retroclubkit.team.service;

import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.repository.TeamRepository;

import org.retroclubkit.web.dto.CreatedNewTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team getById(UUID teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public void saveTeam(CreatedNewTeam createdNewTeam) {
        Team team = convertToEntity(createdNewTeam);
        teamRepository.save(team);
    }

    private Team convertToEntity(CreatedNewTeam createdNewTeam) {
        return Team.builder()
                .id(UUID.randomUUID())
                .name(createdNewTeam.getName())
                .country(createdNewTeam.getCountry())
                .build();
    }

    public void deleteTeamById(UUID id) {
        teamRepository.deleteById(id);
    }
}
