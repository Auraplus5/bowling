package com.kevin.bowling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer score;
    private List<Integer> scores = new ArrayList<>();

    public List<Integer> addScores(Integer val) {
        this.scores.add(val);
        return scores;
    }

    public void resetScores() {
        this.scores.clear();
        this.score = 0;
    }

}
