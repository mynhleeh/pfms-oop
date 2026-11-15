package com.pfms.service.impl.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DuplicateEntityException;
import com.pfms.domain.exception.EntityNotFoundException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.memory.MemoryCategoryRepository;
import com.pfms.infrastructure.memory.MemoryNeedGroupRepository;
import com.pfms.infrastructure.memory.MemoryTransactionRepository;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.category.CreateCategoryRequestDTO;
import com.pfms.presentation.dto.category.CreateNeedGroupRequestDTO;
import com.pfms.presentation.dto.category.NeedGroupResponseDTO;
import com.pfms.presentation.dto.category.RenameCategoryRequestDTO;
import com.pfms.presentation.dto.category.RenameNeedGroupRequestDTO;
import com.pfms.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử CategoryServiceImpl")
class CategoryServiceImplTest {

    private MemoryCategoryRepository categoryRepository;
    private MemoryNeedGroupRepository needGroupRepository;
    private MemoryTransactionRepository transactionRepository;
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = new MemoryCategoryRepository();
        needGroupRepository = new MemoryNeedGroupRepository();
        transactionRepository = new MemoryTransactionRepository();
        categoryService = new CategoryServiceImpl(categoryRepository, needGroupRepository, transactionRepository);
    }

    @Nested
    @DisplayName("Kiểm thử tiêm phụ thuộc qua hàm khởi tạo (Constructor Tests)")
    class ConstructorTests {

        @Test
        @DisplayName("Khởi tạo thành công khi tất cả kho lưu trữ hợp lệ và cài đặt đúng interface CategoryService")
        void shouldInitializeSuccessfullyWithValidRepositories() {
            assertNotNull(categoryService);
            assertTrue(categoryService instanceof CategoryService);
        }

        @Test
        @DisplayName("Ném NullPointerException khi CategoryRepository là null")
        void shouldThrowExceptionWhenCategoryRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new CategoryServiceImpl(null, needGroupRepository, transactionRepository)
            );
            assertTrue(ex.getMessage().contains("CategoryRepository"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi NeedGroupRepository là null")
        void shouldThrowExceptionWhenNeedGroupRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new CategoryServiceImpl(categoryRepository, null, transactionRepository)
            );
            assertTrue(ex.getMessage().contains("NeedGroupRepository"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi TransactionRepository là null")
        void shouldThrowExceptionWhenTransactionRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new CategoryServiceImpl(categoryRepository, needGroupRepository, null)
            );
            assertTrue(ex.getMessage().contains("TransactionRepository"));
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-01: Tạo danh mục chi tiết (createCategory)")
    class CreateCategoryTests {

        private UUID activeExpenseGroupId;
        private UUID activeIncomeGroupId;

        @BeforeEach
        void initNeedGroups() {
            activeExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup expenseGroup = new NeedGroup(
                    activeExpenseGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(expenseGroup);

            activeIncomeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            NeedGroup incomeGroup = new NeedGroup(
                    activeIncomeGroupId, "Thu nhập chính", TransactionType.INCOME, "briefcase", "#4CAF50");
            needGroupRepository.save(incomeGroup);
        }

        @Test
        @DisplayName("Tạo danh mục chi tiêu thành công: kế thừa EXPENSE từ nhóm cha và lưu trữ bền vững")
        void shouldCreateExpenseCategorySuccessfully() {
            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(activeExpenseGroupId, "Cơm trưa văn phòng");

            CategoryResponseDTO response = categoryService.createCategory(request);

            assertNotNull(response);
            assertNotNull(response.getCategoryId());
            assertEquals("Cơm trưa văn phòng", response.getName());
            assertEquals(activeExpenseGroupId, response.getParentGroupId());
            assertEquals("Ăn uống", response.getParentGroupName());
            assertEquals("EXPENSE", response.getTransactionType());
            assertEquals("ACTIVE", response.getStatus());
            assertNotNull(response.getCreatedAt());

            // Xác minh đã lưu vào kho dữ liệu
            Optional<Category> savedOpt = categoryRepository.findById(response.getCategoryId());
            assertTrue(savedOpt.isPresent());
            Category saved = savedOpt.get();
            assertEquals("Cơm trưa văn phòng", saved.getName());
            assertEquals(activeExpenseGroupId, saved.getNeedGroupId());
            assertEquals(TransactionType.EXPENSE, saved.getTransactionType());
            assertTrue(saved.isActive());
        }

        @Test
        @DisplayName("Tạo danh mục thu nhập thành công: kế thừa INCOME từ nhóm cha")
        void shouldCreateIncomeCategorySuccessfully() {
            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(activeIncomeGroupId, "Lương tháng");

            CategoryResponseDTO response = categoryService.createCategory(request);

            assertNotNull(response);
            assertEquals("Lương tháng", response.getName());
            assertEquals("INCOME", response.getTransactionType());
            assertEquals("Thu nhập chính", response.getParentGroupName());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng ở hai đầu tên danh mục khi tạo mới")
        void shouldTrimWhitespaceWhenCreatingCategory() {
            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(
                    activeExpenseGroupId, "   Cà phê sáng   ");

            CategoryResponseDTO response = categoryService.createCategory(request);

            assertEquals("Cà phê sáng", response.getName());
        }

        @Test
        @DisplayName("Ném NullPointerException khi request là null")
        void shouldThrowExceptionWhenRequestIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> categoryService.createCategory(null)
            );
            assertTrue(ex.getMessage().contains("Yêu cầu tạo danh mục"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên danh mục rỗng hoặc toàn khoảng trắng")
        void shouldThrowExceptionWhenNameIsBlank(String blankName) {
            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(activeExpenseGroupId, blankName);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.createCategory(request)
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên danh mục vượt quá 50 ký tự")
        void shouldThrowExceptionWhenNameExceedsMaxLength() {
            String longName = "A".repeat(51);
            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(activeExpenseGroupId, longName);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.createCategory(request)
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi nhóm nhu cầu cha không tồn tại")
        void shouldThrowExceptionWhenParentGroupNotFound() {
            UUID nonExistentGroupId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(nonExistentGroupId, "Khoản mới");

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.createCategory(request)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_CAT_005) khi nhóm nhu cầu cha đang ở trạng thái DELETED")
        void shouldThrowExceptionWhenParentGroupIsDeleted() {
            UUID deletedGroupId = UUID.fromString("00000000-0000-0000-0000-000000000003");
            NeedGroup deletedGroup = new NeedGroup(
                    deletedGroupId, "Nhóm cũ", TransactionType.EXPENSE, "folder", "#9E9E9E");
            deletedGroup.markDeleted(2);
            needGroupRepository.save(deletedGroup);

            CreateCategoryRequestDTO request = new CreateCategoryRequestDTO(deletedGroupId, "Khoản chi cũ");

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.createCategory(request)
            );
            assertEquals(ErrorCode.ERR_CAT_005, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) khi tên danh mục trùng lặp trong cùng nhóm cha (không phân biệt hoa thường)")
        void shouldThrowExceptionWhenCategoryNameDuplicatedInSameParentGroup() {
            categoryService.createCategory(new CreateCategoryRequestDTO(activeExpenseGroupId, "Trà sữa"));

            CreateCategoryRequestDTO duplicateRequest = new CreateCategoryRequestDTO(
                    activeExpenseGroupId, "   TRÀ SỮA   ");

            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.createCategory(duplicateRequest)
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Cho phép trùng tên danh mục nếu thuộc hai nhóm nhu cầu cha khác nhau")
        void shouldAllowSameNameInDifferentParentGroups() {
            CategoryResponseDTO cat1 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeExpenseGroupId, "Khoản khác"));
            CategoryResponseDTO cat2 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeIncomeGroupId, "Khoản khác"));

            assertNotNull(cat1);
            assertNotNull(cat2);
            assertNotEquals(cat1.getCategoryId(), cat2.getCategoryId());
            assertEquals("Khoản khác", cat1.getName());
            assertEquals("Khoản khác", cat2.getName());
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-02: Đổi tên và chuyển nhóm danh mục (renameCategory)")
    class RenameCategoryTests {

        private UUID activeExpenseGroupId;
        private UUID anotherExpenseGroupId;
        private UUID activeIncomeGroupId;
        private UUID existingCategoryId;

        @BeforeEach
        void initTestData() {
            activeExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup expenseGroup = new NeedGroup(
                    activeExpenseGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(expenseGroup);

            anotherExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000004");
            NeedGroup anotherExpenseGroup = new NeedGroup(
                    anotherExpenseGroupId, "Đi lại", TransactionType.EXPENSE, "car", "#2196F3");
            needGroupRepository.save(anotherExpenseGroup);

            activeIncomeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            NeedGroup incomeGroup = new NeedGroup(
                    activeIncomeGroupId, "Thu nhập chính", TransactionType.INCOME, "briefcase", "#4CAF50");
            needGroupRepository.save(incomeGroup);

            CategoryResponseDTO created = categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeExpenseGroupId, "Cơm trưa"));
            existingCategoryId = created.getCategoryId();
        }

        @Test
        @DisplayName("Đổi tên danh mục thành công trong cùng nhóm cha")
        void shouldRenameCategorySuccessfully() {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(existingCategoryId, "Cơm trưa văn phòng");

            CategoryResponseDTO response = categoryService.renameCategory(request);

            assertNotNull(response);
            assertEquals(existingCategoryId, response.getCategoryId());
            assertEquals("Cơm trưa văn phòng", response.getName());
            assertEquals(activeExpenseGroupId, response.getParentGroupId());
            assertEquals("Ăn uống", response.getParentGroupName());

            // Kiểm tra trạng thái lưu trữ bền vững
            Category saved = categoryRepository.findById(existingCategoryId).orElseThrow();
            assertEquals("Cơm trưa văn phòng", saved.getName());
        }

        @Test
        @DisplayName("Tự động cắt tỉa khoảng trắng ở hai đầu tên danh mục mới")
        void shouldTrimWhitespaceWhenRenamingCategory() {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "   Cơm trưa cao cấp   ");

            CategoryResponseDTO response = categoryService.renameCategory(request);

            assertEquals("Cơm trưa cao cấp", response.getName());
        }

        @Test
        @DisplayName("Cho phép giữ nguyên tên hiện tại hoặc chỉ đổi hoa thường mà không bị báo lỗi trùng lặp")
        void shouldAllowKeepingSameNameWithoutConflict() {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(existingCategoryId, "CƠM TRƯA");

            CategoryResponseDTO response = categoryService.renameCategory(request);

            assertNotNull(response);
            assertEquals("CƠM TRƯA", response.getName());
        }

        @Test
        @DisplayName("Chuyển nhóm nhu cầu cha thành công khi nhóm mới cùng loại EXPENSE")
        void shouldChangeParentGroupSuccessfullyWhenSameTransactionType() {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "Cơm trưa", anotherExpenseGroupId);

            CategoryResponseDTO response = categoryService.renameCategory(request);

            assertNotNull(response);
            assertEquals(anotherExpenseGroupId, response.getParentGroupId());
            assertEquals("Đi lại", response.getParentGroupName());

            Category saved = categoryRepository.findById(existingCategoryId).orElseThrow();
            assertEquals(anotherExpenseGroupId, saved.getNeedGroupId());
        }

        @Test
        @DisplayName("Vừa đổi tên vừa chuyển nhóm cha cùng lúc thành công")
        void shouldRenameAndChangeParentGroupSimultaneously() {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "Vé xe buýt", anotherExpenseGroupId);

            CategoryResponseDTO response = categoryService.renameCategory(request);

            assertNotNull(response);
            assertEquals("Vé xe buýt", response.getName());
            assertEquals(anotherExpenseGroupId, response.getParentGroupId());
            assertEquals("Đi lại", response.getParentGroupName());
        }

        @Test
        @DisplayName("Ném NullPointerException khi request là null")
        void shouldThrowExceptionWhenRequestIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> categoryService.renameCategory(null)
            );
            assertTrue(ex.getMessage().contains("Yêu cầu đổi tên danh mục"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên mới rỗng hoặc toàn khoảng trắng")
        void shouldThrowExceptionWhenNameIsBlank(String blankName) {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(existingCategoryId, blankName);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên mới vượt quá 50 ký tự")
        void shouldThrowExceptionWhenNameExceedsMaxLength() {
            String longName = "A".repeat(51);
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(existingCategoryId, longName);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi danh mục không tồn tại")
        void shouldThrowExceptionWhenCategoryNotFound() {
            UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(nonExistentId, "Tên mới");

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi nhóm cha mới không tồn tại")
        void shouldThrowExceptionWhenTargetParentGroupNotFound() {
            UUID nonExistentGroupId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "Tên mới", nonExistentGroupId);

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_CAT_003) khi chuyển sang nhóm cha đang ở trạng thái DELETED")
        void shouldThrowExceptionWhenTargetParentGroupIsDeleted() {
            UUID deletedGroupId = UUID.fromString("00000000-0000-0000-0000-000000000005");
            NeedGroup deletedGroup = new NeedGroup(
                    deletedGroupId, "Nhóm lưu trữ", TransactionType.EXPENSE, "archive", "#757575");
            deletedGroup.markDeleted(3);
            needGroupRepository.save(deletedGroup);

            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "Tên mới", deletedGroupId);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_CAT_003, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_CAT_001) khi chuyển sang nhóm cha khác loại thu chi (EXPENSE -> INCOME)")
        void shouldThrowExceptionWhenTargetParentGroupHasDifferentTransactionType() {
            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "Tên mới", activeIncomeGroupId);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) khi tên mới trùng lặp với danh mục khác trong cùng nhóm")
        void shouldThrowExceptionWhenNewNameDuplicatedInSameParentGroup() {
            categoryService.createCategory(new CreateCategoryRequestDTO(activeExpenseGroupId, "Cà phê"));

            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "   CÀ PHÊ   ");

            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) khi chuyển nhóm và tên bị trùng với danh mục đã có trong nhóm đích")
        void shouldThrowExceptionWhenNewNameDuplicatedInNewParentGroup() {
            categoryService.createCategory(new CreateCategoryRequestDTO(anotherExpenseGroupId, "Xăng xe"));

            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(
                    existingCategoryId, "Xăng xe", anotherExpenseGroupId);

            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi đổi tên danh mục đang ở trạng thái DELETED")
        void shouldThrowExceptionWhenRenamingDeletedCategory() {
            Category category = categoryRepository.findById(existingCategoryId).orElseThrow();
            category.markDeleted();
            categoryRepository.save(category);

            RenameCategoryRequestDTO request = new RenameCategoryRequestDTO(existingCategoryId, "Tên mới");

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.renameCategory(request)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-04: Khôi phục danh mục đã xóa (restoreCategory)")
    class RestoreCategoryTests {

        private UUID activeExpenseGroupId;
        private UUID deletedExpenseGroupId;
        private UUID activeIncomeGroupId;
        private UUID deletedCategoryId;

        @BeforeEach
        void initTestData() {
            activeExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup expenseGroup = new NeedGroup(
                    activeExpenseGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(expenseGroup);

            deletedExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000005");
            NeedGroup deletedGroup = new NeedGroup(
                    deletedExpenseGroupId, "Nhóm lưu trữ", TransactionType.EXPENSE, "archive", "#757575");
            deletedGroup.markDeleted(2);
            needGroupRepository.save(deletedGroup);

            activeIncomeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            NeedGroup incomeGroup = new NeedGroup(
                    activeIncomeGroupId, "Thu nhập chính", TransactionType.INCOME, "briefcase", "#4CAF50");
            needGroupRepository.save(incomeGroup);

            // Tạo danh mục rồi đánh dấu DELETED
            deletedCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000010");
            Category category = new Category(
                    deletedCategoryId, "Trà chiều", activeExpenseGroupId, TransactionType.EXPENSE, "", "");
            category.markDeleted();
            categoryRepository.save(category);
        }

        @Test
        @DisplayName("Khôi phục danh mục thành công khi nhóm cha đang ACTIVE")
        void shouldRestoreCategorySuccessfully() {
            CategoryResponseDTO response = categoryService.restoreCategory(deletedCategoryId);

            assertNotNull(response);
            assertEquals(deletedCategoryId, response.getCategoryId());
            assertEquals("Trà chiều", response.getName());
            assertEquals(activeExpenseGroupId, response.getParentGroupId());
            assertEquals("Ăn uống", response.getParentGroupName());
            assertEquals("EXPENSE", response.getTransactionType());
            assertEquals("ACTIVE", response.getStatus());

            // Xác minh trong kho lưu trữ
            Category saved = categoryRepository.findById(deletedCategoryId).orElseThrow();
            assertTrue(saved.isActive());
            assertEquals("ACTIVE", saved.getStatus().name());
        }

        @Test
        @DisplayName("Ném NullPointerException khi categoryId là null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> categoryService.restoreCategory(null)
            );
            assertTrue(ex.getMessage().contains("Mã danh mục không được để null"));
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi categoryId không tồn tại")
        void shouldThrowExceptionWhenCategoryNotFound() {
            UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000099");

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.restoreCategory(nonExistentId)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi danh mục đang ở trạng thái ACTIVE (không cần khôi phục)")
        void shouldThrowExceptionWhenCategoryIsAlreadyActive() {
            CategoryResponseDTO activeCat = categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeExpenseGroupId, "Cà phê sáng"));

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.restoreCategory(activeCat.getCategoryId())
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_CAT_004) khi nhóm nhu cầu cha đang ở trạng thái DELETED")
        void shouldThrowExceptionWhenParentNeedGroupIsDeleted() {
            UUID catInDeletedGroupId = UUID.fromString("00000000-0000-0000-0000-000000000011");
            Category catInDeletedGroup = new Category(
                    catInDeletedGroupId, "Mục trong nhóm xóa", deletedExpenseGroupId, TransactionType.EXPENSE, "", "");
            catInDeletedGroup.markDeleted();
            categoryRepository.save(catInDeletedGroup);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.restoreCategory(catInDeletedGroupId)
            );
            assertEquals(ErrorCode.ERR_CAT_004, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("Hãy khôi phục nhóm trước"));
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) khi trùng tên với một danh mục đang ACTIVE khác trong cùng nhóm")
        void shouldThrowExceptionWhenConflictWithExistingActiveCategoryNameInSameGroup() {
            // Tạo một danh mục active mới có tên trùng "Trà chiều"
            categoryService.createCategory(new CreateCategoryRequestDTO(activeExpenseGroupId, "Trà chiều"));

            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.restoreCategory(deletedCategoryId)
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Cho phép khôi phục nếu tên trùng thuộc nhóm nhu cầu cha khác")
        void shouldAllowRestoringWhenSameNameExistsInDifferentGroup() {
            // Tạo một danh mục active trùng tên nhưng trong nhóm INCOME
            categoryService.createCategory(new CreateCategoryRequestDTO(activeIncomeGroupId, "Trà chiều"));

            CategoryResponseDTO response = categoryService.restoreCategory(deletedCategoryId);

            assertNotNull(response);
            assertEquals("ACTIVE", response.getStatus());
            assertEquals(activeExpenseGroupId, response.getParentGroupId());
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-03: Xóa danh mục (deleteCategory)")
    class DeleteCategoryTests {

        private UUID activeExpenseGroupId;
        private UUID categoryWithNoTxId;
        private UUID categoryWithTxId;
        private Wallet testWallet;

        @BeforeEach
        void initTestData() {
            activeExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup expenseGroup = new NeedGroup(
                    activeExpenseGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(expenseGroup);

            // Tạo danh mục 1: không có giao dịch
            CategoryResponseDTO cat1 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeExpenseGroupId, "Trái cây"));
            categoryWithNoTxId = cat1.getCategoryId();

            // Tạo danh mục 2: có giao dịch
            CategoryResponseDTO cat2 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeExpenseGroupId, "Cơm trưa"));
            categoryWithTxId = cat2.getCategoryId();

            // Tạo danh mục 3 để đảm bảo nhóm luôn còn ít nhất 1 mục hoạt động sau khi xóa
            categoryService.createCategory(
                    new CreateCategoryRequestDTO(activeExpenseGroupId, "Nước ngọt"));

            // Tạo ví và giao dịch liên kết với categoryWithTxId
            testWallet = new Wallet(UUID.randomUUID(), "Ví tiền mặt", "Ví thử nghiệm", true);
            Category categoryForTx = categoryRepository.findById(categoryWithTxId).orElseThrow();
            IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                    UUID.randomUUID(),
                    TransactionType.EXPENSE,
                    testWallet,
                    categoryForTx,
                    Money.of("35000.00"),
                    TransactionDate.now(),
                    "Ăn trưa bình dân",
                    null
            );
            transactionRepository.save(tx);
        }

        @Test
        @DisplayName("Xóa vật lý (xóa cứng) thành công khi danh mục chưa từng phát sinh giao dịch")
        void shouldHardDeleteCategoryWhenNoTransactionsExist() {
            categoryService.deleteCategory(categoryWithNoTxId);

            // Xác minh đã bị xóa hoàn toàn khỏi kho lưu trữ
            Optional<Category> opt = categoryRepository.findById(categoryWithNoTxId);
            assertTrue(opt.isEmpty());

            // Nhóm cha vẫn còn 2 danh mục hoạt động
            assertEquals(2, categoryRepository.countActiveByNeedGroupId(activeExpenseGroupId));
        }

        @Test
        @DisplayName("Xóa mềm bảo toàn lịch sử khi danh mục đã từng phát sinh giao dịch")
        void shouldSoftDeleteCategoryWhenTransactionsExist() {
            categoryService.deleteCategory(categoryWithTxId);

            // Xác minh bản ghi vẫn tồn tại nhưng trạng thái chuyển sang DELETED
            Optional<Category> opt = categoryRepository.findById(categoryWithTxId);
            assertTrue(opt.isPresent());
            Category saved = opt.get();
            assertEquals("DELETED", saved.getStatus().name());
            assertFalse(saved.isActive());
            assertTrue(saved.isDeleted());

            // Nhóm cha chỉ còn 2 danh mục active
            assertEquals(2, categoryRepository.countActiveByNeedGroupId(activeExpenseGroupId));
        }

        @Test
        @DisplayName("Ném NullPointerException khi categoryId là null")
        void shouldThrowExceptionWhenCategoryIdIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> categoryService.deleteCategory(null)
            );
            assertTrue(ex.getMessage().contains("Mã danh mục không được để null"));
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi categoryId không tồn tại")
        void shouldThrowExceptionWhenCategoryNotFound() {
            UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000099");

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.deleteCategory(nonExistentId)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi danh mục đã ở trạng thái DELETED")
        void shouldThrowExceptionWhenCategoryIsAlreadyDeleted() {
            // Xóa mềm categoryWithTxId lần đầu
            categoryService.deleteCategory(categoryWithTxId);

            // Gọi xóa lần 2 sẽ ném EntityNotFoundException
            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.deleteCategory(categoryWithTxId)
            );
            assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Cho phép xóa danh mục hoạt động duy nhất còn lại trong nhóm theo BR-FIN-04 và Phương án A")
        void shouldAllowDeletingLastActiveCategoryInNeedGroup() {
            UUID singleCatGroupId = UUID.fromString("00000000-0000-0000-0000-000000000009");
            NeedGroup singleCatGroup = new NeedGroup(
                    singleCatGroupId, "Học tập", TransactionType.EXPENSE, "book", "#3F51B5");
            needGroupRepository.save(singleCatGroup);

            CategoryResponseDTO onlyCat = categoryService.createCategory(
                    new CreateCategoryRequestDTO(singleCatGroupId, "Sách giáo trình"));

            categoryService.deleteCategory(onlyCat.getCategoryId());

            assertTrue(categoryRepository.findById(onlyCat.getCategoryId()).isEmpty());
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-04: Kết xuất cây phân cấp danh mục 3 cấp (getCategoryHierarchy)")
    class GetCategoryHierarchyTests {

        @Test
        @DisplayName("Trả về danh sách rỗng khi hệ thống chưa có dữ liệu nhóm nhu cầu")
        void shouldReturnEmptyListWhenNoDataExists() {
            List<CategoryHierarchyResponseDTO> hierarchy = categoryService.getCategoryHierarchy();

            assertNotNull(hierarchy);
            assertTrue(hierarchy.isEmpty());
        }

        @Test
        @DisplayName("Kết xuất đúng cấu trúc cây phân cấp gồm nhóm cha và các danh mục con active")
        void shouldReturnHierarchyWithGroupAndChildren() {
            UUID expenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup expenseGroup = new NeedGroup(
                    expenseGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(expenseGroup);

            categoryService.createCategory(new CreateCategoryRequestDTO(expenseGroupId, "Cơm trưa"));
            categoryService.createCategory(new CreateCategoryRequestDTO(expenseGroupId, "Cà phê"));

            List<CategoryHierarchyResponseDTO> hierarchy = categoryService.getCategoryHierarchy();

            assertNotNull(hierarchy);
            assertEquals(1, hierarchy.size());

            CategoryHierarchyResponseDTO groupDto = hierarchy.get(0);
            assertEquals("EXPENSE", groupDto.getTransactionType());
            assertEquals(expenseGroupId, groupDto.getNeedGroupId());
            assertEquals("Ăn uống", groupDto.getNeedGroupName());
            assertEquals("ACTIVE", groupDto.getNeedGroupStatus());
            assertEquals(2, groupDto.getCategories().size());

            List<String> childNames = groupDto.getCategories().stream()
                    .map(CategoryResponseDTO::getName)
                    .toList();
            assertTrue(childNames.contains("Cơm trưa"));
            assertTrue(childNames.contains("Cà phê"));
        }

        @Test
        @DisplayName("Bao gồm cả nhóm nhu cầu đã xóa mềm (DELETED) trong cây phân cấp theo OOD 03")
        void shouldIncludeDeletedNeedGroupsInHierarchy() {
            UUID activeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup activeGroup = new NeedGroup(
                    activeGroupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(activeGroup);

            UUID deletedGroupId = UUID.fromString("00000000-0000-0000-0000-000000000005");
            NeedGroup deletedGroup = new NeedGroup(
                    deletedGroupId, "Nhóm cũ", TransactionType.EXPENSE, "archive", "#757575");
            deletedGroup.markDeleted(2);
            needGroupRepository.save(deletedGroup);

            List<CategoryHierarchyResponseDTO> hierarchy = categoryService.getCategoryHierarchy();

            assertEquals(2, hierarchy.size());
            assertEquals(activeGroupId, hierarchy.get(0).getNeedGroupId());
            assertEquals("ACTIVE", hierarchy.get(0).getNeedGroupStatus());
            assertEquals(deletedGroupId, hierarchy.get(1).getNeedGroupId());
            assertEquals("DELETED", hierarchy.get(1).getNeedGroupStatus());
        }

        @Test
        @DisplayName("Loại trừ các danh mục đã bị xóa (DELETED) khỏi danh sách con của nhóm")
        void shouldExcludeDeletedCategories() {
            UUID groupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            NeedGroup group = new NeedGroup(
                    groupId, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722");
            needGroupRepository.save(group);

            CategoryResponseDTO cat1 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(groupId, "Cơm trưa"));
            CategoryResponseDTO cat2 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(groupId, "Trái cây"));

            // Xóa cứng cat2 (vì chưa có giao dịch)
            categoryService.deleteCategory(cat2.getCategoryId());

            List<CategoryHierarchyResponseDTO> hierarchy = categoryService.getCategoryHierarchy();

            assertEquals(1, hierarchy.size());
            assertEquals(1, hierarchy.get(0).getCategories().size());
            assertEquals(cat1.getCategoryId(), hierarchy.get(0).getCategories().get(0).getCategoryId());
        }

        @Test
        @DisplayName("Xử lý nhóm nhu cầu chưa có danh mục con một cách an toàn (trả về list rỗng)")
        void shouldHandleGroupWithNoChildrenGracefully() {
            UUID groupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            NeedGroup group = new NeedGroup(
                    groupId, "Thu nhập chính", TransactionType.INCOME, "briefcase", "#4CAF50");
            needGroupRepository.save(group);

            List<CategoryHierarchyResponseDTO> hierarchy = categoryService.getCategoryHierarchy();

            assertEquals(1, hierarchy.size());
            assertNotNull(hierarchy.get(0).getCategories());
            assertTrue(hierarchy.get(0).getCategories().isEmpty());
        }

        @Test
        @DisplayName("Sắp xếp ổn định cây phân cấp theo TransactionType và tên bảng chữ cái")
        void shouldSortHierarchyDeterministically() {
            UUID expenseGroupId2 = UUID.fromString("00000000-0000-0000-0000-000000000004");
            needGroupRepository.save(new NeedGroup(expenseGroupId2, "Đi lại", TransactionType.EXPENSE, "car", "#2196F3"));

            UUID expenseGroupId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
            needGroupRepository.save(new NeedGroup(expenseGroupId1, "Ăn uống", TransactionType.EXPENSE, "utensils", "#FF5722"));

            UUID incomeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            needGroupRepository.save(new NeedGroup(incomeGroupId, "Lương thưởng", TransactionType.INCOME, "money", "#4CAF50"));

            // Tạo các danh mục con không theo thứ tự bảng chữ cái
            categoryService.createCategory(new CreateCategoryRequestDTO(expenseGroupId1, "Trà sữa"));
            categoryService.createCategory(new CreateCategoryRequestDTO(expenseGroupId1, "Bún bò"));
            categoryService.createCategory(new CreateCategoryRequestDTO(expenseGroupId1, "Cơm tấm"));

            List<CategoryHierarchyResponseDTO> hierarchy = categoryService.getCategoryHierarchy();

            assertEquals(3, hierarchy.size());

            // Nhóm 1: EXPENSE - Ăn uống
            assertEquals("EXPENSE", hierarchy.get(0).getTransactionType());
            assertEquals("Ăn uống", hierarchy.get(0).getNeedGroupName());

            // Các danh mục con phải được xếp theo alphabet: Bún bò, Cơm tấm, Trà sữa
            List<String> names = hierarchy.get(0).getCategories().stream()
                    .map(CategoryResponseDTO::getName)
                    .toList();
            assertEquals(List.of("Bún bò", "Cơm tấm", "Trà sữa"), names);

            // Nhóm 2: EXPENSE - Đi lại
            assertEquals("EXPENSE", hierarchy.get(1).getTransactionType());
            assertEquals("Đi lại", hierarchy.get(1).getNeedGroupName());

            // Nhóm 3: INCOME - Lương thưởng
            assertEquals("INCOME", hierarchy.get(2).getTransactionType());
            assertEquals("Lương thưởng", hierarchy.get(2).getNeedGroupName());
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-01 (3a): Tạo nhóm nhu cầu mới (createNeedGroup)")
    class CreateNeedGroupTests {

        @Test
        @DisplayName("Tạo nhóm nhu cầu mới thành công với đầy đủ thuộc tính và lưu vào kho")
        void shouldCreateNeedGroupSuccessfully() {
            NeedGroupResponseDTO created = categoryService.createNeedGroup(
                    new CreateNeedGroupRequestDTO("  expense ", "  Sức khỏe  ", " heart ", " #E91E63 "));

            assertNotNull(created.getNeedGroupId());
            assertEquals("Sức khỏe", created.getName());
            assertEquals("EXPENSE", created.getTransactionType());
            assertEquals("ACTIVE", created.getStatus());
            assertEquals("heart", created.getIcon());
            assertEquals("#E91E63", created.getColor());
            assertNotNull(created.getCreatedAt());

            Optional<NeedGroup> saved = needGroupRepository.findById(created.getNeedGroupId());
            assertTrue(saved.isPresent());
            assertEquals("Sức khỏe", saved.get().getName());
            assertEquals("heart", saved.get().getIcon());
            assertEquals("#E91E63", saved.get().getColor());
        }

        @Test
        @DisplayName("Cho phép tạo trùng tên giữa EXPENSE và INCOME, hoặc trùng tên nhóm đã bị DELETED")
        void shouldAllowSameNameAcrossTypesOrWhenExistingIsDeleted() {
            NeedGroupResponseDTO expenseFirst = categoryService.createNeedGroup(
                    new CreateNeedGroupRequestDTO("EXPENSE", "Đầu tư"));
            NeedGroupResponseDTO incomeSameName = categoryService.createNeedGroup(
                    new CreateNeedGroupRequestDTO("INCOME", "Đầu tư"));

            assertNotEquals(expenseFirst.getNeedGroupId(), incomeSameName.getNeedGroupId());

            categoryService.createNeedGroup(new CreateNeedGroupRequestDTO("EXPENSE", "Khác"));
            categoryService.deleteNeedGroup(expenseFirst.getNeedGroupId());

            NeedGroupResponseDTO recreated = categoryService.createNeedGroup(
                    new CreateNeedGroupRequestDTO("EXPENSE", "Đầu tư"));
            assertNotEquals(expenseFirst.getNeedGroupId(), recreated.getNeedGroupId());
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) khi trùng tên nhóm đang hoạt động cùng loại")
        void shouldThrowDuplicateExceptionWhenActiveGroupNameExistsInSameType() {
            categoryService.createNeedGroup(new CreateNeedGroupRequestDTO("EXPENSE", "Ăn uống"));

            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.createNeedGroup(new CreateNeedGroupRequestDTO("EXPENSE", "  ĂN UỐNG "))
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @ParameterizedTest(name = "Loại thu chi không hợp lệ \"{0}\" bị từ chối với ERR_CAT_001")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "TRANSFER", "OTHER"})
        @DisplayName("Ném BusinessException(ERR_CAT_001) khi transactionType null hoặc không hợp lệ")
        void shouldThrowBusinessExceptionWhenTransactionTypeInvalid(String invalidType) {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.createNeedGroup(new CreateNeedGroupRequestDTO(invalidType, "Nhóm mới"))
            );
            assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
        }

        @ParameterizedTest(name = "Tên nhóm không hợp lệ \"{0}\" bị từ chối với ERR_NAM_001")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên nhóm null, rỗng hoặc quá 50 ký tự")
        void shouldThrowBusinessExceptionWhenNameInvalid(String invalidName) {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.createNeedGroup(new CreateNeedGroupRequestDTO("EXPENSE", invalidName))
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên nhóm vượt quá 50 ký tự")
        void shouldThrowBusinessExceptionWhenNameTooLong() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.createNeedGroup(new CreateNeedGroupRequestDTO("INCOME", "N".repeat(51)))
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném NullPointerException khi request createNeedGroup là null")
        void shouldThrowExceptionWhenRequestIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> categoryService.createNeedGroup(null)
            );
            assertTrue(ex.getMessage().contains("Yêu cầu"));
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-02: Đổi tên và cập nhật hiển thị nhóm (renameNeedGroup)")
    class RenameNeedGroupTests {

        private UUID group1Id;
        private UUID group2Id;

        @BeforeEach
        void initGroups() {
            group1Id = UUID.fromString("00000000-0000-0000-0000-000000000011");
            group2Id = UUID.fromString("00000000-0000-0000-0000-000000000012");
            needGroupRepository.save(new NeedGroup(group1Id, "Sức khỏe", TransactionType.EXPENSE, "heart", "#E91E63"));
            needGroupRepository.save(new NeedGroup(group2Id, "Giải trí", TransactionType.EXPENSE, "film", "#9C27B0"));
        }

        @Test
        @DisplayName("Đổi tên bằng DTO 2 tham số giữ nguyên icon và color hiện có")
        void shouldRenameWithoutOverwritingAppearance() {
            NeedGroupResponseDTO updated = categoryService.renameNeedGroup(
                    new RenameNeedGroupRequestDTO(group1Id, "  Y tế & Sức khỏe  "));

            assertEquals(group1Id, updated.getNeedGroupId());
            assertEquals("Y tế & Sức khỏe", updated.getName());
            assertEquals("heart", updated.getIcon());
            assertEquals("#E91E63", updated.getColor());
        }

        @Test
        @DisplayName("Cập nhật tên cùng icon/color từng phần hoặc toàn phần và cho phép trùng tên chính mình")
        void shouldRenameAndUpdateAppearanceIncludingPartialOrSameNameCaseChange() {
            NeedGroupResponseDTO onlyColorUpdated = categoryService.renameNeedGroup(
                    new RenameNeedGroupRequestDTO(group1Id, "SỨC KHỎE", null, "#009688"));

            assertEquals("SỨC KHỎE", onlyColorUpdated.getName());
            assertEquals("heart", onlyColorUpdated.getIcon());
            assertEquals("#009688", onlyColorUpdated.getColor());

            NeedGroupResponseDTO clearedAppearance = categoryService.renameNeedGroup(
                    new RenameNeedGroupRequestDTO(group1Id, "Y tế", "", ""));

            assertEquals("Y tế", clearedAppearance.getName());
            assertEquals("", clearedAppearance.getIcon());
            assertEquals("", clearedAppearance.getColor());
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) khi tên mới trùng nhóm đang dùng khác cùng loại")
        void shouldThrowDuplicateExceptionWhenNewNameMatchesAnotherActiveGroup() {
            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.renameNeedGroup(new RenameNeedGroupRequestDTO(group1Id, "  giải trí  "))
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_NAM_001) khi tên mới không hợp lệ")
        void shouldThrowBusinessExceptionWhenNewNameIsInvalid() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.renameNeedGroup(new RenameNeedGroupRequestDTO(group1Id, "   "))
            );
            assertEquals(ErrorCode.ERR_NAM_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi nhóm không tồn tại hoặc đã bị xóa mềm")
        void shouldThrowNotFoundWhenGroupMissingOrDeleted() {
            UUID unknownId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            assertEquals(ErrorCode.ERR_NOT_FOUND, assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.renameNeedGroup(new RenameNeedGroupRequestDTO(unknownId, "Tên mới"))
            ).getErrorCode());

            NeedGroup deletedGroup = needGroupRepository.findById(group1Id).orElseThrow();
            deletedGroup.markDeleted(2);
            needGroupRepository.save(deletedGroup);

            assertEquals(ErrorCode.ERR_NOT_FOUND, assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.renameNeedGroup(new RenameNeedGroupRequestDTO(group1Id, "Tên mới"))
            ).getErrorCode());
        }

        @Test
        @DisplayName("Ném NullPointerException khi request renameNeedGroup là null")
        void shouldThrowExceptionWhenRequestIsNull() {
            assertThrows(NullPointerException.class, () -> categoryService.renameNeedGroup(null));
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-03: Khôi phục nhóm nhu cầu đã xóa (restoreNeedGroup)")
    class RestoreNeedGroupTests {

        private UUID deletedGroupId;
        private UUID childCategoryId;

        @BeforeEach
        void initDeletedGroup() {
            deletedGroupId = UUID.fromString("00000000-0000-0000-0000-000000000021");
            NeedGroup group = new NeedGroup(deletedGroupId, "Du lịch", TransactionType.EXPENSE, "plane", "#00BCD4");
            group.markDeleted(2);
            needGroupRepository.save(group);

            childCategoryId = UUID.fromString("00000000-0000-0000-0000-000000000221");
            Category child = Category.reconstitute(
                    childCategoryId, "Vé máy bay", deletedGroupId, TransactionType.EXPENSE,
                    EntityStatus.DELETED, "", "", group.getCreatedAt());
            categoryRepository.save(child);
        }

        @Test
        @DisplayName("Khôi phục riêng nhóm từ DELETED sang ACTIVE mà giữ nguyên danh mục con đã xóa (UC-CAT-03)")
        void shouldRestoreGroupOnlyWithoutAutoRestoringDeletedChildren() {
            NeedGroupResponseDTO restored = categoryService.restoreNeedGroup(deletedGroupId);

            assertEquals(deletedGroupId, restored.getNeedGroupId());
            assertEquals("ACTIVE", restored.getStatus());
            assertTrue(restored.isActive());

            NeedGroup savedGroup = needGroupRepository.findById(deletedGroupId).orElseThrow();
            assertTrue(savedGroup.isActive());

            Category childAfterRestore = categoryRepository.findById(childCategoryId).orElseThrow();
            assertTrue(childAfterRestore.isDeleted());
        }

        @Test
        @DisplayName("Ném DuplicateEntityException(ERR_NAM_002) nếu đã có nhóm ACTIVE trùng tên trong cùng loại")
        void shouldThrowDuplicateExceptionWhenActiveGroupWithSameNameExists() {
            categoryService.createNeedGroup(new CreateNeedGroupRequestDTO("EXPENSE", "Du lịch"));

            DuplicateEntityException ex = assertThrows(
                    DuplicateEntityException.class,
                    () -> categoryService.restoreNeedGroup(deletedGroupId)
            );
            assertEquals(ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi nhóm không tồn tại hoặc đang ACTIVE")
        void shouldThrowNotFoundWhenMissingOrAlreadyActive() {
            assertEquals(ErrorCode.ERR_NOT_FOUND, assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.restoreNeedGroup(UUID.randomUUID())
            ).getErrorCode());

            NeedGroupResponseDTO activeGroup = categoryService.createNeedGroup(
                    new CreateNeedGroupRequestDTO("EXPENSE", "Mua sắm"));
            assertEquals(ErrorCode.ERR_NOT_FOUND, assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.restoreNeedGroup(activeGroup.getNeedGroupId())
            ).getErrorCode());
        }

        @Test
        @DisplayName("Ném NullPointerException khi needGroupId là null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThrows(NullPointerException.class, () -> categoryService.restoreNeedGroup(null));
        }
    }

    @Nested
    @DisplayName("Ca sử dụng UC-CAT-04: Xóa nhóm nhu cầu (deleteNeedGroup)")
    class DeleteNeedGroupTests {

        private UUID removableGroupId;
        private UUID backupExpenseGroupId;
        private UUID soleIncomeGroupId;
        private Wallet testWallet;

        @BeforeEach
        void initGroups() {
            removableGroupId = UUID.fromString("00000000-0000-0000-0000-000000000031");
            backupExpenseGroupId = UUID.fromString("00000000-0000-0000-0000-000000000032");
            soleIncomeGroupId = UUID.fromString("00000000-0000-0000-0000-000000000033");

            needGroupRepository.save(new NeedGroup(removableGroupId, "Giải trí", TransactionType.EXPENSE, "", ""));
            needGroupRepository.save(new NeedGroup(backupExpenseGroupId, "Thiết yếu", TransactionType.EXPENSE, "", ""));
            needGroupRepository.save(new NeedGroup(soleIncomeGroupId, "Lương chính", TransactionType.INCOME, "", ""));

            testWallet = new Wallet(UUID.randomUUID(), "Ví tiền mặt", "", true);
        }

        @Test
        @DisplayName("Xóa cứng nhóm và mọi danh mục con khi chưa có giao dịch nào liên kết")
        void shouldHardDeleteGroupAndAllChildrenWhenNoTransactionsExist() {
            CategoryResponseDTO child1 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(removableGroupId, "Xem phim"));
            CategoryResponseDTO child2 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(removableGroupId, "Nghe nhạc"));

            categoryService.deleteNeedGroup(removableGroupId);

            assertTrue(needGroupRepository.findById(removableGroupId).isEmpty());
            assertTrue(categoryRepository.findById(child1.getCategoryId()).isEmpty());
            assertTrue(categoryRepository.findById(child2.getCategoryId()).isEmpty());
        }

        @Test
        @DisplayName("Xóa mềm nhóm và danh mục có giao dịch, đồng thời xóa cứng danh mục con chưa có giao dịch")
        void shouldSoftDeleteGroupAndTxChildrenWhileHardDeletingUnusedChildren() {
            CategoryResponseDTO unusedChild = categoryService.createCategory(
                    new CreateCategoryRequestDTO(removableGroupId, "Sách truyện"));
            CategoryResponseDTO usedChild = categoryService.createCategory(
                    new CreateCategoryRequestDTO(removableGroupId, "Du lịch"));

            Category usedCategoryEntity = categoryRepository.findById(usedChild.getCategoryId()).orElseThrow();
            transactionRepository.save(new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, testWallet, usedCategoryEntity,
                    Money.of("500000.00"), TransactionDate.now(), "Vé tham quan", null));

            categoryService.deleteNeedGroup(removableGroupId);

            NeedGroup deletedGroup = needGroupRepository.findById(removableGroupId).orElseThrow();
            assertTrue(deletedGroup.isDeleted());

            Category softDeletedChild = categoryRepository.findById(usedChild.getCategoryId()).orElseThrow();
            assertTrue(softDeletedChild.isDeleted());

            assertTrue(categoryRepository.findById(unusedChild.getCategoryId()).isEmpty());
        }

        @Test
        @DisplayName("Ném BusinessException(ERR_CAT_002) khi xóa nhóm đang dùng cuối cùng của một loại thu chi và giữ nguyên con")
        void shouldThrowBusinessExceptionWhenDeletingLastActiveGroupInType() {
            CategoryResponseDTO incomeCat = categoryService.createCategory(
                    new CreateCategoryRequestDTO(soleIncomeGroupId, "Lương tháng"));

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> categoryService.deleteNeedGroup(soleIncomeGroupId)
            );
            assertEquals(ErrorCode.ERR_CAT_002, ex.getErrorCode());

            assertTrue(needGroupRepository.findById(soleIncomeGroupId).orElseThrow().isActive());
            assertTrue(categoryRepository.findById(incomeCat.getCategoryId()).orElseThrow().isActive());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException(ERR_NOT_FOUND) khi nhóm không tồn tại hoặc đã xóa")
        void shouldThrowNotFoundWhenGroupMissingOrAlreadyDeleted() {
            assertEquals(ErrorCode.ERR_NOT_FOUND, assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.deleteNeedGroup(UUID.randomUUID())
            ).getErrorCode());

            categoryService.deleteNeedGroup(removableGroupId);
            assertEquals(ErrorCode.ERR_NOT_FOUND, assertThrows(
                    EntityNotFoundException.class,
                    () -> categoryService.deleteNeedGroup(removableGroupId)
            ).getErrorCode());
        }

        @Test
        @DisplayName("Ném NullPointerException khi needGroupId là null")
        void shouldThrowExceptionWhenIdIsNull() {
            assertThrows(NullPointerException.class, () -> categoryService.deleteNeedGroup(null));
        }

        @Test
        @DisplayName("Hoàn nguyên nhóm nhu cầu và danh mục con khi xóa nhóm phát sinh StorageException")
        void shouldRollbackNeedGroupAndChildrenWhenStorageExceptionOccurs() {
            CategoryResponseDTO child1 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(removableGroupId, "Danh mục test 1"));
            CategoryResponseDTO child2 = categoryService.createCategory(
                    new CreateCategoryRequestDTO(removableGroupId, "Danh mục test 2"));

            MemoryNeedGroupRepository failingNeedGroupRepo = new MemoryNeedGroupRepository() {
                @Override
                public boolean deleteById(UUID id) {
                    throw new StorageException("Lỗi mô phỏng xóa nhóm trên đĩa", new java.io.IOException());
                }
            };
            for (NeedGroup g : needGroupRepository.findAll()) {
                failingNeedGroupRepo.save(g);
            }

            CategoryService failingService = new CategoryServiceImpl(
                    categoryRepository, failingNeedGroupRepo, transactionRepository);

            assertThrows(StorageException.class, () -> failingService.deleteNeedGroup(removableGroupId));

            assertTrue(failingNeedGroupRepo.findById(removableGroupId).isPresent());
            assertTrue(categoryRepository.findById(child1.getCategoryId()).isPresent(),
                    "Danh mục con 1 phải được khôi phục nguyên vẹn");
            assertTrue(categoryRepository.findById(child2.getCategoryId()).isPresent(),
                    "Danh mục con 2 phải được khôi phục nguyên vẹn");
        }
    }
}
