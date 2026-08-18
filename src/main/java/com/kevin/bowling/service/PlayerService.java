package com.kevin.bowling.service;

import com.kevin.bowling.dto.PlayerScoreTableResponse;
import com.kevin.bowling.dto.PlayerPointsResponse;
import com.kevin.bowling.entity.Player;
import com.kevin.bowling.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public List<Player> addPlayer(Player player) {
        playerRepository.save(player);
        return playerRepository.findAll();
    }

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    public PlayerPointsResponse getPoints(Long id) {
        Player player = playerRepository.findById(id).orElseThrow();
        return new PlayerPointsResponse(
                player.getName(),
                player.getScore()
        );
    }

    public PlayerScoreTableResponse getTable(Long id) {
        Player player = playerRepository.findById(id).orElseThrow();
        return new PlayerScoreTableResponse(
                player.getName(),
                player.getScores()
        );

    }
}
