package com.pfms.service.impl.category;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bộ hỗ trợ nội bộ dựng cây phân cấp danh mục thu chi và ánh xạ DTO phản hồi.
 */
final class CategoryHierarchySupport {

    // region Constructors
    private CategoryHierarchySupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static List<CategoryHierarchyResponseDTO> buildCategoryHierarchy(
            NeedGroupRepository needGroupRepository,
            CategoryRepository categoryRepository) {

        List<NeedGroup> activeGroups = needGroupRepository.findActiveGroups();
        if (activeGroups.isEmpty()) {
            return Collections.emptyList();
        }

        List<Category> activeCategories = categoryRepository.findActiveCategories();
        Map<UUID, List<Category>> categoriesByGroupId = activeCategories.stream()
                .collect(Collectors.groupingBy(Category::getNeedGroupId));

        return activeGroups.stream()
                .sorted(Comparator.comparing((NeedGroup group) -> group.getTransactionType().name())
                        .thenComparing(NeedGroup::getName, String.CASE_INSENSITIVE_ORDER))
                .map(group -> toHierarchyDto(
                        group, categoriesByGroupId.getOrDefault(group.getNeedGroupId(), Collections.emptyList())))
                .toList();
    }

    static CategoryResponseDTO toResponseDTO(Category category, NeedGroup parentGroup) {
        return new CategoryResponseDTO(
                category.getCategoryId(),
                category.getName(),
                parentGroup.getNeedGroupId(),
                parentGroup.getName(),
                category.getTransactionType().name(),
                category.getStatus().name(),
                LocalDateTime.now());
    }
    // endregion

    // region Helpers
    private static CategoryHierarchyResponseDTO toHierarchyDto(NeedGroup group, List<Category> children) {
        List<CategoryResponseDTO> childDtos = children.stream()
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(category -> toResponseDTO(category, group))
                .toList();

        return new CategoryHierarchyResponseDTO(
                group.getTransactionType().name(),
                group.getNeedGroupId(),
                group.getName(),
                group.getStatus().name(),
                childDtos);
    }
    // endregion
}

