package com.kevin.bowling.dto;

import java.util.List;

public record PlayerScoreTableResponse(
        String name,
        List<Integer> points
) {
}
