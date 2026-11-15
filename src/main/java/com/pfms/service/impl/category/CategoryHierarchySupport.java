package com.pfms.service.impl.category;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bộ hỗ trợ nội bộ dựng cây phân cấp danh mục 3 cấp và ánh xạ thực thể sang DTO phản hồi.
 */
final class CategoryHierarchySupport {

    // region Constants
    private static final Locale VIETNAMESE_LOCALE = Locale.forLanguageTag("vi-VN");
    // endregion

    // region Constructors
    private CategoryHierarchySupport() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static List<CategoryHierarchyResponseDTO> buildCategoryHierarchy(
            NeedGroupRepository needGroupRepository,
            CategoryRepository categoryRepository) {

        Objects.requireNonNull(needGroupRepository, "NeedGroupRepository không được để null");
        Objects.requireNonNull(categoryRepository, "CategoryRepository không được để null");

        List<NeedGroup> groups = needGroupRepository.findAll();
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }

        Collator viCollator = Collator.getInstance(VIETNAMESE_LOCALE);
        viCollator.setStrength(Collator.SECONDARY);

        List<Category> categories = categoryRepository.findAll();
        Map<UUID, List<Category>> categoriesByGroupId = categories.stream()
                .collect(Collectors.groupingBy(Category::getNeedGroupId));

        Comparator<NeedGroup> groupOrder = Comparator
                .comparing((NeedGroup group) -> group.getTransactionType().name())
                .thenComparing(Comparator.comparing(NeedGroup::isActive).reversed())
                .thenComparing(NeedGroup::getName, viCollator);

        Comparator<Category> categoryOrder = Comparator
                .comparing(Category::isActive).reversed()
                .thenComparing(Category::getName, viCollator);

        return groups.stream()
                .sorted(groupOrder)
                .map(group -> toHierarchyDto(
                        group,
                        categoriesByGroupId.getOrDefault(group.getNeedGroupId(), Collections.emptyList()),
                        categoryOrder))
                .toList();
    }

    static CategoryResponseDTO toResponseDTO(Category category, NeedGroup parentGroup) {
        Objects.requireNonNull(category, "Danh mục không được để null");
        Objects.requireNonNull(parentGroup, "Nhóm nhu cầu cha không được để null");

        return new CategoryResponseDTO(
                category.getCategoryId(),
                category.getName(),
                parentGroup.getNeedGroupId(),
                parentGroup.getName(),
                category.getTransactionType().name(),
                category.getStatus().name(),
                category.getCreatedAt());
    }

    static NeedGroupResponseDTO toNeedGroupResponseDTO(NeedGroup group) {
        Objects.requireNonNull(group, "Nhóm nhu cầu không được để null");

        return new NeedGroupResponseDTO(
                group.getNeedGroupId(),
                group.getName(),
                group.getTransactionType().name(),
                group.getStatus().name(),
                group.getIcon(),
                group.getColor(),
                group.getCreatedAt());
    }
    // endregion

    // region Helpers
    private static CategoryHierarchyResponseDTO toHierarchyDto(
            NeedGroup group,
            List<Category> children,
            Comparator<Category> categoryOrder) {

        List<CategoryResponseDTO> childDtos = children.stream()
                .sorted(categoryOrder)
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
