package com.group2.volunteer.controller;

import com.group2.volunteer.entity.Category;
import com.group2.volunteer.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/category_list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category_form";
    }

//    @PostMapping("/save")
//    public String saveCategory(@ModelAttribute("category") Category category, RedirectAttributes redirectAttributes) {
//        categoryService.save(category);
//        redirectAttributes.addFlashAttribute("message", "Lưu danh mục thành công!");
//        return "redirect:/admin/categories";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        model.addAttribute("category", categoryService.findById(id));
//        return "admin/category_form";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        categoryService.deleteById(id);
//        redirectAttributes.addFlashAttribute("message", "Đã xóa danh mục!");
//        return "redirect:/admin/categories";
//    }
}
