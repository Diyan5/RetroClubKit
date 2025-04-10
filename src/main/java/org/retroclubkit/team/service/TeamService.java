package org.retroclubkit.team.service;

import org.retroclubkit.exception.DomainException;
import org.retroclubkit.exception.TeamAlreadyExistException;
import org.retroclubkit.team.model.Team;
import org.retroclubkit.team.repository.TeamRepository;

import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.web.dto.CreatedNewTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team getById(UUID teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new DomainException("Team with id [%s] does not exist.".formatted(teamId)));
    }

    public List<Team> getAllTeamsWhichIsNotDeleted() {
        return teamRepository.findAllByIsDeletedFalse();
    }

    public void saveTeam(CreatedNewTeam createdNewTeam) {
        Team team = convertToEntity(createdNewTeam);
        teamRepository.save(team);
    }

    private Team convertToEntity(CreatedNewTeam createdNewTeam) {
        teamRepository.findByName(createdNewTeam.getName()).ifPresent(t ->
        { throw new TeamAlreadyExistException("Team with name [%s] already exist.".formatted(createdNewTeam.getName())); });
        return Team.builder()
                .name(createdNewTeam.getName())
                .country(createdNewTeam.getCountry())
                .build();
    }

    public void deleteTeamById(UUID id) {
//        teamRepository.deleteById(id);
        Team team = teamRepository.findById(id).orElseThrow(() -> new DomainException("Team with id [%s] does not exist.".formatted(id)));
        team.setDeleted(true);
        List<Tshirt> tshirts = team.getTshirts();
        for (Tshirt tshirt : tshirts) {
            tshirt.setDeleted(true);
            tshirt.setAvailable(false);
        }
        teamRepository.save(team);
    }

    public UUID findByName(String name) {
        Optional<Team> byName = teamRepository.findByName(name);
        return byName.get().getId();
    }
}
