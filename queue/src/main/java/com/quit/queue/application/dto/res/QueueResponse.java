package com.quit.queue.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class QueueResponse {
    private UUID storeId;
    private List<UserScore> userScores = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserScore {
        private Long userId;
        private Integer score;
    }

    public QueueResponse(UUID storeId) {
        this.storeId = storeId;
        this.userScores = new ArrayList<>();
    }

    public void addUserScore(Long userId, Integer score) {
        if (this.userScores == null) {
            this.userScores = new ArrayList<>();
        }
        this.userScores.add(new UserScore(userId, score));
    }
}
