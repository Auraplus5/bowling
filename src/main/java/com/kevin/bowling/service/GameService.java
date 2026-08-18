package com.kevin.bowling.service;

import com.kevin.bowling.dto.GameResponse;
import com.kevin.bowling.dto.PlayerPointsResponse;
import com.kevin.bowling.entity.BowlingFrame;
import com.kevin.bowling.entity.Game;
import com.kevin.bowling.entity.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final PlayerService playerService;
    private Game game;

    public GameResponse startGame() {
        List<Player> players = playerService.getPlayers();
        if (players.isEmpty()) {
            throw new RuntimeException("Please add players");
        }
        game = new Game();
        for (Player player : players) {
            player.resetScores();
            playerService.savePlayer(player);
        }
        game.setPlayers(players);
        game.setFrame(1);
        game.setCurrentPlayer(players.getFirst());
        return toGameResponse();
    }

    public GameResponse throwBall(Integer score) {
        if (game == null) {
            throw new RuntimeException("Game is not started");
        }
        Player player = game.getCurrentPlayer();
        List<BowlingFrame> bowlingFrame = buildFrames(player.getScores());
        if (isGameOver(bowlingFrame)) {
            throw new RuntimeException("Game over");
        }
        player.addScores(score);
        bowlingFrame = buildFrames(player.getScores());
        player.setScore(calculateScore(bowlingFrame));
        playerService.savePlayer(player);
        BowlingFrame last = bowlingFrame.getLast();
        game.setFrame(last.isComplete() ? Math.min(last.getNumber() + 1, 10) : last.getNumber());
        if (bowlingFrame.getLast().isComplete()) {
            game.advancePlayer();
        }
        return toGameResponse();
    }

    private int calculateScore(List<BowlingFrame> bowlingFrames) {
        int score = 0;
        for (int i = 0; i < bowlingFrames.size(); i++) {
            BowlingFrame currentFrame = bowlingFrames.get(i);
            if(!bowlingFrames.get(i).isComplete()) {
                break;
            }
            if (currentFrame.isTenth()) {
                for (int j = 0; j < currentFrame.rollCount(); j++) {
                    score += currentFrame.getRoll(j);
                }
            } else if (currentFrame.isStrike()) {
                Integer strikeScore = calculateStrikeScore(bowlingFrames, i);
                if (strikeScore == null) break;
                score += strikeScore;
            } else if (currentFrame.isSpare()) {
                BowlingFrame next = i + 1 < bowlingFrames.size() ? bowlingFrames.get(i + 1) : null;
                if (next == null) break;
                score += 10 + next.getRoll(0);
            } else {
                score += currentFrame.getRoll(0) + currentFrame.getRoll(1);
            }
        }
        return score;
    }


    private List<BowlingFrame> buildFrames(List<Integer> rolls) {
        List<BowlingFrame> frames = new ArrayList<>();
        int i = 0;
        for(int frameNum = 1; frameNum <= 10 && i < rolls.size(); frameNum++) {
            BowlingFrame bowlingFrame = new BowlingFrame(frameNum);
            frames.add(bowlingFrame);

            if (frameNum < 10) {
               bowlingFrame.addRoll(rolls.get(i++));
               if(!bowlingFrame.isStrike() && i < rolls.size()) {
                   bowlingFrame.addRoll(rolls.get(i++));
               }
            }

            if (bowlingFrame.isTenth()) {
                while(!bowlingFrame.isComplete() && i < rolls.size()) {
                    bowlingFrame.addRoll(rolls.get(i++));
                }
            }

            if(!bowlingFrame.isComplete()) {
                break;
            }

        }

        return frames;
    }


    private boolean isGameOver(List<BowlingFrame> frames) {
        if (frames.size() < 10) return false;
        BowlingFrame tenth = frames.get(9);
        return tenth.isTenth() && tenth.isComplete();
    }

    private Integer calculateStrikeScore(List<BowlingFrame> bowlingFrames, int i) {
        BowlingFrame next = i + 1 < bowlingFrames.size() ? bowlingFrames.get(i + 1) : null;
        if (next == null) return null;
        if (next.isStrike() && !next.isTenth()) {
            BowlingFrame nextNext = i + 2 < bowlingFrames.size() ? bowlingFrames.get(i + 2) : null;
            if (nextNext == null) return null;
            return 10 + next.getRoll(0) + nextNext.getRoll(0);
        }
        if (next.rollCount() < 2) return null;
        return 10 + next.getRoll(0) + next.getRoll(1);
    }

    private GameResponse toGameResponse() {
        List<PlayerPointsResponse> scores = game.getPlayers().stream()
                .map(p -> new PlayerPointsResponse(p.getName(), p.getScore()))
                .toList();
        return new GameResponse(game.getFrame(), game.getCurrentPlayer().getName(), scores);
    }
}
