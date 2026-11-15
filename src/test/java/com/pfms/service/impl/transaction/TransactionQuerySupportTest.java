package com.pfms.service.impl.transaction;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.memory.MemoryCategoryRepository;
import com.pfms.infrastructure.memory.MemoryTransactionRepository;
import com.pfms.infrastructure.memory.MemoryWalletRepository;
import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("TransactionQuerySupport Unit Tests")
class TransactionQuerySupportTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<TransactionQuerySupport> constructor =
                TransactionQuerySupport.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("Should match keyword case-insensitively regardless of default Locale (tr-TR)")
    void shouldMatchKeywordCaseInsensitivelyUnderTurkishLocale() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            MemoryTransactionRepository txnRepo = new MemoryTransactionRepository();
            MemoryWalletRepository walletRepo = new MemoryWalletRepository();
            MemoryCategoryRepository catRepo = new MemoryCategoryRepository();

            Wallet wallet = new Wallet(
                    UUID.randomUUID(), "Ví chính", "", false,
                    LocalDateTime.now(), new ActiveWalletState(), Money.of("1000000.00"));
            Category category = new Category(
                    UUID.randomUUID(), "Internet", UUID.randomUUID(),
                    TransactionType.EXPENSE, "wifi.png", "#0000FF");

            IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, wallet, category,
                    Money.of("100000.00"), TransactionDate.now(), "INTERNET PAYMENT", null);

            txnRepo.save(tx);
            walletRepo.save(wallet);
            catRepo.save(category);

            FilterTransactionsRequestDTO request = new FilterTransactionsRequestDTO(
                    null, null, null, null, null, "internet");

            TransactionListResponseDTO result = TransactionQuerySupport.filterTransactions(
                    txnRepo, walletRepo, catRepo, request);

            assertEquals(1, result.getTotalRecords());
            assertEquals("INTERNET PAYMENT", result.getItems().get(0).getNote());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}


