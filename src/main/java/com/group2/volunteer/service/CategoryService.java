package com.group2.volunteer.service;

import com.group2.volunteer.dto.CategoryRequest;
import com.group2.volunteer.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Long id);

    void save(CategoryRequest categoryRequest);

    void deleteById(Long id);
}
