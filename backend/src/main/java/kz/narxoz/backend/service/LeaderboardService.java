package kz.narxoz.backend.service;

import kz.narxoz.backend.dto.response.LeaderboardResponse;
import kz.narxoz.backend.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

        final int finalMinAge = minAge;
        final int finalMaxAge = maxAge;

        return childRepository.findAll().stream()
                .filter(child -> child.getAge() != null
                        && child.getAge() >= finalMinAge
                        && child.getAge() <= finalMaxAge)
                .sorted(Comparator.comparing(c -> -c.getXpPoints()))
                .limit(10)
                .map(child -> new LeaderboardResponse(child.getName(), child.getXpPoints()))
                .toList();
    }
}
