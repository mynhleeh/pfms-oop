package com.pfms.domain.model.category;

import java.math.*;
import java.util.* ;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

public class NetCashFlow {
    private final BigDecimal amount ;
    private final String currency = "VNĐ";
    
    public NetCashFlow(BigDecimal amount){
        if(amount == null  ) {
            throw new BusinessException(ErrorCode.ERR_FIN_001) ; 
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static NetCashFlow of(BigDecimal totalIncome,BigDecimal totalExpense) {
        if (totalIncome == null ||totalExpense == null ) {
            throw new BusinessException(ErrorCode.ERR_FIN_001);
        }

        return new NetCashFlow(totalIncome - totalExpense);
    }

    public BigDecimal getAmount(){
        return amount ;
    }

    public String getCurrency(){
        return currency ;
    }

    public boolean isPositive(){
        return amount.signum() > 0 ;
    }

    public boolean isNegative(){
        return amount.signum() < 0 ;
    }

    public boolean isZero(){
        return amount.signum() == 0 ;
    }

    public String format(){
        return (amount.signum() >= 0 ? "+" : "-") + amount.toPlainString() + " " + currency;
    }

    public int compareTo(NetCashFlow other){
        return this.amount.compareTo(other.getAmount()) ;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NetCashFlow other)) return false;
        return amount.equals(other.amount)
                && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

}