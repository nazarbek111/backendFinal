package kz.narxoz.backend.controller;

import kz.narxoz.backend.dto.response.LeaderboardResponse;
import kz.narxoz.backend.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard(
            @RequestParam(required = false) String age) {

        return ResponseEntity.ok(
                leaderboardService.getLeaderboard(age)
        );
    }
}