package com.becoder.service;

import java.util.List;

import com.becoder.dto.CategoryDto;
import com.becoder.dto.CategoryResponses;
import com.becoder.entity.Category;

public interface CategoryService {

	public Boolean saveCategory(CategoryDto categoryDto);
	
	public List<CategoryDto> getAllCategory();

	public List<CategoryResponses> getActiveCategory();

	public CategoryDto getCategoryById(Integer id);

	public Boolean deleteCategory(Integer id);
}
