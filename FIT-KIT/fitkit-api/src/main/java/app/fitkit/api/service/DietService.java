package app.fitkit.api.service;

import app.fitkit.api.dto.DietLogRequest;
import app.fitkit.api.dto.DietLogResponse;
import app.fitkit.api.dto.ParsedItem;
import app.fitkit.api.entity.DietLog;
import app.fitkit.api.entity.User;
import app.fitkit.api.exception.ResourceNotFoundException;
import app.fitkit.api.repository.DietRepository;
import app.fitkit.api.repository.UserRepository;
import app.fitkit.api.util.DietNLPAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;
    private final UserRepository userRepository;

    @Transactional
    public DietLogResponse saveOrUpdateDailyCard(UUID userId, DietLogRequest request) {
        
        // 1. Verify User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Determine target date (Default to today if not provided)
        LocalDate targetDate = request.loggedDate() != null ? request.loggedDate() : LocalDate.now();

        // 3. RUN THE NLP ANALYZER
        List<ParsedItem> parsedItems = DietNLPAnalyzer.extractItemsFromNote(request.journalNote());

        // 4. The "Upsert" Logic
        DietLog dietLog = dietRepository.findByUserIdAndLoggedDate(userId, targetDate)
                .orElseGet(() -> {
                    // If no card exists for today, build a fresh one
                    DietLog newLog = new DietLog();
                    newLog.setUser(user);
                    newLog.setLoggedDate(targetDate);
                    newLog.setTotalCalories(0);
                    newLog.setTotalProtein(0.0);
                    newLog.setTotalCarbs(0.0);
                    newLog.setTotalFats(0.0);
                    return newLog;
                });

        // 5. Apply the new data (This updates an existing card OR populates a new one)
        dietLog.setJournalNote(request.journalNote());
        
        // The @Convert annotation we built earlier will automatically turn this into PostgreSQL JSONB!
        dietLog.setExtractedItems(parsedItems);

        // 6. Save to Database
        DietLog savedLog = dietRepository.save(dietLog);

        // 7. Return the response
        return new DietLogResponse(
                savedLog.getId(),
                savedLog.getJournalNote(),
                savedLog.getTotalCalories(),
                savedLog.getTotalProtein(),
                savedLog.getTotalCarbs(),
                savedLog.getTotalFats(),
                savedLog.getLoggedDate(),
                savedLog.getExtractedItems()
        );
    }
}