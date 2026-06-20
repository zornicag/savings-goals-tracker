package app.web;

import app.model.dto.category.CategoryForm;
import app.model.entity.category.Category;
import app.service.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    @GetMapping("/add")
    public String getAddCategory(Model model) {
        if (!model.containsAttribute("categoryForm")) {
            model.addAttribute("categoryForm", new CategoryForm());
        }
        return "category-add";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("categoryForm") CategoryForm categoryForm,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "category-add";
        }

        Category category = new Category();
        setField(category, "name", categoryForm.getName().trim());
        setField(category, "description", categoryForm.getDescription());

        categoryService.save(category);
        return "redirect:/categories";
    }

    private void setField(Category category, String fieldName, Object value) {
        try {
            Field field = Category.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(category, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot set field " + fieldName, e);
        }
    }
}

