package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.response.LeaderboardResponse;
import kz.narxoz.backend.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final ChildRepository childRepository;

    public List<LeaderboardResponse> getLeaderboard(String ageGroup) {
        int minAge = 3;
        int maxAge = 8;

        if (ageGroup != null) {
            switch (ageGroup) {
                case "3-5" -> { minAge = 3; maxAge = 5; }
                case "6-8" -> { minAge = 6; maxAge = 8; }
            }
        }

        return childRepository.findLeaderboard(minAge, maxAge, PageRequest.of(0, 10))
                .stream()
                .map(child -> new LeaderboardResponse(child.getName(), child.getXpPoints()))
                .toList();
    }
}