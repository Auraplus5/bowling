package com.kevin.bowling.service;

import com.kevin.bowling.dto.GameResponse;
import com.kevin.bowling.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private GameService gameService;

    private Player alice;
    private Player bob;

    @BeforeEach
    void setUp() {
        alice = new Player(1L, "Alice", 0, new java.util.ArrayList<>());
        bob   = new Player(2L, "Bob",   0, new java.util.ArrayList<>());
        when(playerService.getPlayers()).thenReturn(List.of(alice, bob));
        gameService.startGame();
    }

    @Test
    void startGame_setsFirstPlayerAsCurrent() {
        GameResponse response = gameService.startGame();
        assertThat(response.currentPlayer()).isEqualTo("Alice");
    }

    @Test
    void startGame_setsFrameToOne() {
        GameResponse response = gameService.startGame();
        assertThat(response.currentFrame()).isEqualTo(1);
    }

    @Test
    void startGame_resetsPlayerScores() {
        alice.addScores(5);
        gameService.startGame();
        assertThat(alice.getScores()).isEmpty();
        assertThat(alice.getScore()).isEqualTo(0);
    }

    @Test
    void throwBall_normalRolls_accumulatesScore() {
        gameService.throwBall(4);
        gameService.throwBall(3);
        assertThat(alice.getScore()).isEqualTo(7);
    }

    @Test
    void throwBall_afterCompletedFrame_advancesToNextPlayer() {
        gameService.throwBall(4);
        gameService.throwBall(3); // Alice's frame done
        GameResponse response = gameService.throwBall(5); // Bob's turn
        assertThat(response.currentPlayer()).isEqualTo("Bob");
    }

    @Test
    void throwBall_strike_advancesPlayerAfterOneRoll() {
        GameResponse response = gameService.throwBall(10); // Alice strikes
        assertThat(response.currentPlayer()).isEqualTo("Bob");
    }

    @Test
    void throwBall_strike_scoresCorrectlyOnceNextFrameComplete() {
        gameService.throwBall(10);
        gameService.throwBall(3);
        gameService.throwBall(4);
        gameService.throwBall(3);
        gameService.throwBall(4);
        assertThat(alice.getScore()).isEqualTo(24);
    }

    @Test
    void throwBall_spare_scoresCorrectlyOnceNextRollComplete() {
        gameService.throwBall(7);
        gameService.throwBall(3);
        gameService.throwBall(5);
        gameService.throwBall(2);
        gameService.throwBall(4);
        gameService.throwBall(2);
        assertThat(alice.getScore()).isEqualTo(20);
    }

    @Test
    void throwBall_wrapsBackToFirstPlayerAfterAllComplete() {
        gameService.throwBall(4); gameService.throwBall(3);
        gameService.throwBall(4); gameService.throwBall(3);
        GameResponse response = gameService.throwBall(5);
        assertThat(response.currentPlayer()).isEqualTo("Alice");
    }

    @Test
    void throwBall_afterGameOver_throwsException() {
        when(playerService.getPlayers()).thenReturn(List.of(alice));
        gameService.startGame();
        rollPerfectGame();
        assertThatThrownBy(() -> gameService.throwBall(5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Game over");
    }

    @Test
    void startGame_withNoPlayers_throwsException() {
        when(playerService.getPlayers()).thenReturn(List.of());
        assertThatThrownBy(() -> gameService.startGame())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("players");
    }

    @Test
    void perfectGame_scoreIs300() {
        when(playerService.getPlayers()).thenReturn(List.of(alice));
        gameService.startGame();
        rollPerfectGame();
        assertThat(alice.getScore()).isEqualTo(300);
    }

    @Test
    void allSpares_scoreIs150() {
        when(playerService.getPlayers()).thenReturn(List.of(alice));
        gameService.startGame();
        for (int i = 0; i < 9; i++) {
            gameService.throwBall(5);
            gameService.throwBall(5);
        }
        gameService.throwBall(5);
        gameService.throwBall(5);
        gameService.throwBall(5);
        assertThat(alice.getScore()).isEqualTo(150);
    }


    @Test
    void gutterGame_scoreIsZero() {
        when(playerService.getPlayers()).thenReturn(List.of(alice));
        gameService.startGame();
        for (int i = 0; i < 20; i++) {
            gameService.throwBall(0);
        }
        assertThat(alice.getScore()).isEqualTo(0);
    }

    private void rollPerfectGame() {
        for (int i = 0; i < 12; i++) {
            gameService.throwBall(10);
        }
    }
}
