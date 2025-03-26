package org.retroclubkit;

import org.retroclubkit.team.service.TeamService;
import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Size;
import org.retroclubkit.tshirt.service.TshirtService;
import org.retroclubkit.user.service.UserService;
import org.retroclubkit.web.dto.CreatedNewTeam;
import org.retroclubkit.web.dto.CreatedNewTshirt;
import org.retroclubkit.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CommandRunner implements CommandLineRunner {
    private final TeamService teamService;
    private final TshirtService tshirtService;
    private final UserService userService;

    @Autowired
    public CommandRunner(TeamService teamService, TshirtService tshirtService, UserService userService) {
        this.teamService = teamService;
        this.tshirtService = tshirtService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        //Created Users
        RegisterRequest user = RegisterRequest.builder()
                .username("Dido123").firstName("Diyan").lastName("Paskalev")
                .email("didotest@gmail.com").password("123123").confirmPassword("123123")
                .build();
        userService.register(user);

        RegisterRequest user2 = RegisterRequest.builder()
                .username("Gosho123").firstName("Gosho").lastName("Petrov")
                .email("gosho@gmail.com").password("123123").confirmPassword("123123")
                .build();
        userService.register(user2);

        //Created teams
        CreatedNewTeam team = CreatedNewTeam.builder()
                .country("Spain").name("Spain").build();
        teamService.saveTeam(team);

        CreatedNewTeam team2 = CreatedNewTeam.builder()
                .country("Spain").name("Real Madrid").build();
        teamService.saveTeam(team2);

        CreatedNewTeam team3 = CreatedNewTeam.builder()
                .country("Spain").name("Barcelona").build();
        teamService.saveTeam(team3);

        CreatedNewTeam team4 = CreatedNewTeam.builder()
                .country("England").name("Chelsea").build();
        teamService.saveTeam(team4);

        //Created t-shirts
        CreatedNewTshirt tshirt = CreatedNewTshirt.builder()
                .name("Spain 2024 Home").price(BigDecimal.valueOf(129.99))
                .image("https://i.imgur.com/atfwpAw.jpg").category(Category.NATIONAL)
                .sizes(List.of(Size.S, Size.M, Size.L, Size.XL))
                .isAvailable(true).teamId(teamService.findByName("Spain")).build();
        tshirtService.saveTshirt(tshirt);

        CreatedNewTshirt tshirt2 = CreatedNewTshirt.builder()
                .name("Real Madrid 2024 Home").price(BigDecimal.valueOf(119.99))
                .image("https://imgur.com/UcaOs9Y.jpg").category(Category.NEW)
                .sizes(List.of(Size.S, Size.M, Size.L))
                .isAvailable(true).teamId(teamService.findByName("Real Madrid")).build();
        tshirtService.saveTshirt(tshirt2);

        CreatedNewTshirt tshirt3 = CreatedNewTshirt.builder()
                .name("Barcelona 2024 Home").price(BigDecimal.valueOf(119.99))
                .image("https://imgur.com/TPfUphC.jpg").category(Category.NEW)
                .sizes(List.of(Size.XS, Size.S, Size.M, Size.L, Size.XL))
                .isAvailable(true).teamId(teamService.findByName("Barcelona")).build();
        tshirtService.saveTshirt(tshirt3);

        CreatedNewTshirt tshirt4 = CreatedNewTshirt.builder()
                .name("Barcelona 2004 Home").price(BigDecimal.valueOf(99.99))
                .image("https://imgur.com/spvgEkD.jpg").category(Category.RETRO)
                .sizes(List.of(Size.XS, Size.S, Size.M))
                .isAvailable(true).teamId(teamService.findByName("Barcelona")).build();
        tshirtService.saveTshirt(tshirt4);

        CreatedNewTshirt tshirt5 = CreatedNewTshirt.builder()
                .name("Chelsea 2024 Home").price(BigDecimal.valueOf(199.99))
                .image("https://imgur.com/BeiBik6.jpg").category(Category.NEW)
                .sizes(List.of(Size.XS, Size.S, Size.M, Size.L, Size.XL))
                .isAvailable(true).teamId(teamService.findByName("Chelsea")).build();
        tshirtService.saveTshirt(tshirt5);
    }
}
