package com.group2.volunteer.controller;

import com.group2.volunteer.dto.CategoryRequest;
import com.group2.volunteer.entity.Category;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.service.CategoryService;
import com.group2.volunteer.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProjectRepository projectRepository;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/category_list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryRequest()); // Dùng DTO
        return "admin/category_form";
    }

    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") CategoryRequest categoryRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/category_form";
        }

        categoryService.save(categoryRequest);
        redirectAttributes.addFlashAttribute("message", "Lưu danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);

        CategoryRequest dto = new CategoryRequest();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        model.addAttribute("category", dto);
        return "admin/category_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean isUsedInProjects = projectRepository.existsByCategory_Id(id);

        if (isUsedInProjects) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục này vì đang có dự án thuộc danh mục!");
        } else {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa danh mục thành công!");
        }

        return "redirect:/admin/categories";
    }
}
