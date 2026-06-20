package app.service.savingsgoal;

import app.model.entity.savingsgoal.SavingsGoal;
import app.repository.savingsgoal.SavingsGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
    }

    public List<SavingsGoal> getAllGoals() {
        return savingsGoalRepository.findAll();
    }

    public List<SavingsGoal> getGoalsByUserId(UUID userId) {
        return savingsGoalRepository.findByUser_Id(userId);
    }

    public SavingsGoal getById(UUID id) {
        return savingsGoalRepository.findById(id).orElse(null);
    }

    public SavingsGoal save(SavingsGoal savingsGoal) {
        return savingsGoalRepository.save(savingsGoal);
    }

    public void deleteById(UUID id) {
        savingsGoalRepository.deleteById(id);
    }
}
