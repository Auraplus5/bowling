package com.kevin.bowling.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private List<Player> players;
    private Integer frame;
    private Player currentPlayer;

    public void advancePlayer() {
        int nextPlayer = (players.indexOf(currentPlayer) + 1) % players.size();
        currentPlayer = players.get(nextPlayer);
    }
}
