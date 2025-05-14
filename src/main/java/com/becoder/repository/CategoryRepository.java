package com.becoder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becoder.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	List<Category> findByIsActiveTrue();

	Optional<Category> findByIdAndIsDeletedFalse(Integer id);

	List<Category> findByIsActiveTrueAndIsDeletedFalse();

	List<Category> findByIsDeletedFalse();

}
