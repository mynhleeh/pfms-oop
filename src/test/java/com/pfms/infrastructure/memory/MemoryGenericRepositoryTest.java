package com.pfms.infrastructure.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemoryGenericRepository Unit Tests")
class MemoryGenericRepositoryTest {

    // region Test Doubles
    static class DummyEntity {
        private final UUID id;
        private final String name;

        DummyEntity(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        UUID getId() {
            return this.id;
        }

        String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DummyEntity that = (DummyEntity) o;
            return Objects.equals(id, that.id) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    static class DummyMemoryRepository extends MemoryGenericRepository<DummyEntity, UUID> {
        DummyMemoryRepository(Function<DummyEntity, UUID> idExtractor) {
            super(idExtractor);
        }

        DummyMemoryRepository() {
            super(DummyEntity::getId);
        }
    }
    // endregion

    private DummyMemoryRepository repository;
    private DummyEntity entity1;
    private DummyEntity entity2;

    @BeforeEach
    void setUp() {
        repository = new DummyMemoryRepository();
        entity1 = new DummyEntity(UUID.randomUUID(), "Ví Tiền Mặt");
        entity2 = new DummyEntity(UUID.randomUUID(), "Tài Khoản Ngân Hàng");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Khởi tạo thất bại khi idExtractor là null")
        void shouldThrowNullPointerExceptionWhenIdExtractorIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DummyMemoryRepository(null)
            );
            assertTrue(ex.getMessage().contains("must not be null"));
        }
    }

    @Nested
    @DisplayName("Save Tests")
    class SaveTests {

        @Test
        @DisplayName("Lưu thành công thực thể mới và trả về chính thực thể đó")
        void shouldSaveNewEntitySuccessfully() {
            DummyEntity saved = repository.save(entity1);

            assertEquals(entity1, saved);
            assertEquals(Optional.of(entity1), repository.findById(entity1.getId()));
        }

        @Test
        @DisplayName("Cập nhật thực thể khi mã định danh đã tồn tại")
        void shouldUpdateExistingEntityWhenIdAlreadyExists() {
            repository.save(entity1);
            DummyEntity updated = new DummyEntity(entity1.getId(), "Ví Tiền Mặt Cập Nhật");

            DummyEntity result = repository.save(updated);

            assertEquals(updated, result);
            assertEquals(Optional.of(updated), repository.findById(entity1.getId()));
            assertEquals("Ví Tiền Mặt Cập Nhật", repository.findById(entity1.getId()).orElseThrow().getName());
            assertEquals(1, repository.findAll().size());
        }

        @Test
        @DisplayName("Từ chối lưu thực thể null")
        void shouldThrowNullPointerExceptionWhenSavingNullEntity() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.save(null)
            );
            assertTrue(ex.getMessage().contains("must not be null"));
        }

        @Test
        @DisplayName("Từ chối lưu thực thể có ID là null")
        void shouldThrowNullPointerExceptionWhenEntityIdIsNull() {
            DummyEntity invalidEntity = new DummyEntity(null, "Không có ID");

            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.save(invalidEntity)
            );
            assertTrue(ex.getMessage().contains("must not be null"));
        }
    }

    @Nested
    @DisplayName("FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Tìm thấy thực thể theo mã định danh hợp lệ")
        void shouldReturnEntityWhenIdExists() {
            repository.save(entity1);

            Optional<DummyEntity> found = repository.findById(entity1.getId());

            assertTrue(found.isPresent());
            assertEquals(entity1, found.get());
        }

        @Test
        @DisplayName("Trả về Optional.empty khi mã định danh không tồn tại")
        void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
            Optional<DummyEntity> found = repository.findById(UUID.randomUUID());

            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Từ chối tìm kiếm với mã định danh null")
        void shouldThrowNullPointerExceptionWhenFindByIdWithNullId() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.findById(null)
            );
            assertTrue(ex.getMessage().contains("must not be null"));
        }
    }

    @Nested
    @DisplayName("FindAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Trả về danh sách rỗng khi kho lưu trữ chưa có dữ liệu")
        void shouldReturnEmptyListWhenStorageIsEmpty() {
            List<DummyEntity> all = repository.findAll();

            assertNotNull(all);
            assertTrue(all.isEmpty());
        }

        @Test
        @DisplayName("Trả về toàn bộ danh sách thực thể trong kho lưu trữ")
        void shouldReturnAllEntitiesWhenStorageHasData() {
            repository.save(entity1);
            repository.save(entity2);

            List<DummyEntity> all = repository.findAll();

            assertEquals(2, all.size());
            assertTrue(all.contains(entity1));
            assertTrue(all.contains(entity2));
        }

        @Test
        @DisplayName("Danh sách trả về phải là bất biến (Unmodifiable List)")
        void shouldReturnUnmodifiableList() {
            repository.save(entity1);
            List<DummyEntity> all = repository.findAll();

            DummyEntity newEntity = new DummyEntity(UUID.randomUUID(), "Ví Mới");
            assertThrows(UnsupportedOperationException.class, () -> all.add(newEntity));
        }

        @Test
        @DisplayName("Bản sao phòng vệ không bị biến đổi khi kho lưu trữ thay đổi sau đó")
        void shouldReturnDefensiveSnapshotNotAffectedBySubsequentModifications() {
            repository.save(entity1);
            List<DummyEntity> snapshot = repository.findAll();
            assertEquals(1, snapshot.size());

            repository.save(entity2);
            assertEquals(1, snapshot.size(), "Snapshot không được bị thay đổi khi kho lưu trữ thêm phần tử");

            repository.clear();
            assertEquals(1, snapshot.size(), "Snapshot không được bị xóa khi kho lưu trữ gọi clear()");
        }
    }

    @Nested
    @DisplayName("ExistsById Tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("Trả về true khi thực thể tồn tại")
        void shouldReturnTrueWhenEntityExists() {
            repository.save(entity1);

            assertTrue(repository.existsById(entity1.getId()));
        }

        @Test
        @DisplayName("Trả về false khi thực thể không tồn tại")
        void shouldReturnFalseWhenEntityDoesNotExist() {
            assertFalse(repository.existsById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Từ chối kiểm tra tồn tại với mã định danh null")
        void shouldThrowNullPointerExceptionWhenExistsByIdWithNullId() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.existsById(null)
            );
            assertTrue(ex.getMessage().contains("must not be null"));
        }
    }

    @Nested
    @DisplayName("DeleteById Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Xóa thành công và trả về true khi thực thể tồn tại")
        void shouldDeleteEntityAndReturnTrueWhenIdExists() {
            repository.save(entity1);

            boolean deleted = repository.deleteById(entity1.getId());

            assertTrue(deleted);
            assertFalse(repository.existsById(entity1.getId()));
            assertTrue(repository.findById(entity1.getId()).isEmpty());
        }

        @Test
        @DisplayName("Trả về false khi xóa thực thể không tồn tại")
        void shouldReturnFalseWhenDeletingNonExistentId() {
            boolean deleted = repository.deleteById(UUID.randomUUID());

            assertFalse(deleted);
        }

        @Test
        @DisplayName("Từ chối xóa với mã định danh null")
        void shouldThrowNullPointerExceptionWhenDeleteByIdWithNullId() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.deleteById(null)
            );
            assertTrue(ex.getMessage().contains("must not be null"));
        }
    }

    @Nested
    @DisplayName("Clear Tests")
    class ClearTests {

        @Test
        @DisplayName("Dọn sạch toàn bộ dữ liệu trong bảng băm")
        void shouldRemoveAllEntitiesFromStorage() {
            repository.save(entity1);
            repository.save(entity2);
            assertEquals(2, repository.findAll().size());

            repository.clear();

            assertTrue(repository.findAll().isEmpty());
            assertFalse(repository.existsById(entity1.getId()));
            assertFalse(repository.existsById(entity2.getId()));
        }
    }

    @Nested
    @DisplayName("Concurrency Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("Đảm bảo an toàn luồng khi nhiều luồng cùng lưu thực thể đồng thời")
        void shouldHandleConcurrentSavesSafely() throws InterruptedException {
            int threadCount = 10;
            int itemsPerThread = 50;
            int totalExpected = threadCount * itemsPerThread;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                        for (int j = 0; j < itemsPerThread; j++) {
                            UUID id = UUID.randomUUID();
                            DummyEntity entity = new DummyEntity(id, "Thread-" + threadId + "-Item-" + j);
                            repository.save(entity);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            readyLatch.await();
            startLatch.countDown();
            boolean completed = doneLatch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            assertTrue(completed, "Toàn bộ tác vụ đồng thời phải hoàn tất trong 5 giây");
            assertEquals(totalExpected, repository.findAll().size(), "Số lượng bản ghi trong kho phải chính xác bằng tổng số đã lưu");
        }
    }
}

