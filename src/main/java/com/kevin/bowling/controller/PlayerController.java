package com.kevin.bowling.controller;

import com.kevin.bowling.dto.PlayerScoreTableResponse;
import com.kevin.bowling.dto.PlayerPointsResponse;
import com.kevin.bowling.entity.Player;
import com.kevin.bowling.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/players")
    public List<Player> getPlayers() {
        return playerService.getPlayers();
    }

    @GetMapping("/player/{id}")
    public PlayerPointsResponse getPlayerPoints(@PathVariable Long id) {
        return playerService.getPoints(id);
    }

    @GetMapping("/player-table/{id}")
    public PlayerScoreTableResponse getPlayerPointTable(@PathVariable Long id) {
        return playerService.getTable(id);
    }

    @PostMapping("/players")
    public List<Player> addPlayer(@RequestBody Player player) {
        playerService.addPlayer(player);
        return playerService.getPlayers();
    }
}
