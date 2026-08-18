package com.kevin.bowling.controller;

import com.kevin.bowling.dto.GameResponse;
import com.kevin.bowling.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/start-game")
    public GameResponse startGame() {
        return gameService.startGame();
    }

    @PostMapping("/throw-ball")
    public GameResponse throwBall(@RequestParam Integer score) {
        return gameService.throwBall(score);
    }
}
