package com.pfms.domain.model.wallet;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;

/**
 * Giao diện đại diện cho trạng thái vòng đời của Ví tiền theo mẫu thiết kế GoF
 * State.
 * Định rõ các hành vi tài chính và chuyển đổi vòng đời theo quy tắc nghiệp vụ.
 */
public interface WalletState {

    // region Commands
    void credit(Wallet wallet, Money amount);

    void debit(Wallet wallet, Money amount);

    void archive(Wallet wallet);

    void activate(Wallet wallet);

    void delete(Wallet wallet);
    // endregion

    // region Queries
    EntityStatus getStatus();
    // endregion
}
