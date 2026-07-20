package com.group2.volunteer.service;

import com.group2.volunteer.dto.CategoryRequest;
import com.group2.volunteer.entity.Category;
import com.group2.volunteer.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));
    }

    @Override
    public void save(CategoryRequest request) {
        Category category;

        if (request.getId() != null) {
            category = categoryRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục!"));
        } else {
            category = new Category();
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy danh mục để xóa!");
        }
        categoryRepository.deleteById(id);
    }
}
