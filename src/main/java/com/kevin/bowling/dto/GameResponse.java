package com.kevin.bowling.dto;

import java.util.List;

public record GameResponse(
        int currentFrame,
        String currentPlayer,
        List<PlayerPointsResponse> scores
) {
}
