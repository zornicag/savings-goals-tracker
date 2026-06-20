package app.web;

import app.model.dto.savingsgoal.SavingsGoalForm;
import app.model.entity.category.Category;
import app.model.entity.savingsgoal.SavingsGoal;
import app.model.entity.user.User;
import app.repository.category.CategoryRepository;
import app.repository.user.UserRepository;
import app.service.savingsgoal.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public SavingsGoalController(SavingsGoalService savingsGoalService,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository) {
        this.savingsGoalService = savingsGoalService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String getGoals(Model model) {
        model.addAttribute("goals", savingsGoalService.getAllGoals());
        return "goals";
    }

    @GetMapping("/add")
    public String getAddGoal(Model model) {
        if (!model.containsAttribute("savingsGoalForm")) {
            model.addAttribute("savingsGoalForm", new SavingsGoalForm());
        }
        return "goal-add";
    }

    @PostMapping("/add")
    public String addGoal(@Valid @ModelAttribute("savingsGoalForm") SavingsGoalForm savingsGoalForm,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "goal-add";
        }

        Optional<User> firstUser = userRepository.findAll().stream().findFirst();
        if (firstUser.isEmpty()) {
            model.addAttribute("userError", "Please register a user before creating a goal.");
            return "goal-add";
        }

        Optional<Category> firstCategory = categoryRepository.findAll().stream().findFirst();
        if (firstCategory.isEmpty()) {
            model.addAttribute("userError", "Please create a category before creating a goal.");
            return "goal-add";
        }

        SavingsGoal savingsGoal = new SavingsGoal();
        setField(savingsGoal, "name", savingsGoalForm.getName().trim());
        setField(savingsGoal, "targetAmount", savingsGoalForm.getTargetAmount());
        setField(savingsGoal, "currentAmount", savingsGoalForm.getCurrentAmount());
        setField(savingsGoal, "deadline", savingsGoalForm.getTargetDate() == null ? null : savingsGoalForm.getTargetDate().atStartOfDay());
        setField(savingsGoal, "createdAt", LocalDateTime.now());
        setField(savingsGoal, "user", firstUser.get());
        setField(savingsGoal, "category", firstCategory.get());

        savingsGoalService.save(savingsGoal);
        return "redirect:/goals";
    }

    private void setField(SavingsGoal savingsGoal, String fieldName, Object value) {
        try {
            Field field = SavingsGoal.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(savingsGoal, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot set field " + fieldName, e);
        }
    }
}

