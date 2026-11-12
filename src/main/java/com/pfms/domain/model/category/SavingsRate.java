package com.pfms.domain.model.category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

public class SavingsRate {
    private final BigDecimal rate ;
    private final String unit = "%" ;

    public SavingsRate(BigDecimal rate){
        if(rate == null){
            throw new BusinessException(ErrorCode.ERR_FIN_001,"Tỷ lệ tiết kiệm không hợp lệ");
        }
        this.rate = rate = rate.setScale(2, RoundingMode.HALF_UP);

    }

    public static SavingsRate of(BigDecimal netCashFlow , BigDecimal totalIncome){
        if(totalIncome.signum() > 0){
            return new SavingsRate((netCashFlow.divide(totalIncome)).multiply(BigDecimal.valueOf(100)).setScale(2,RoundingMode.HALF_UP) ) ;
        }

        return new SavingsRate(BigDecimal.valueOf(0)) ; 
    }

    public BigDecimal getRate(){
        return rate ;
    }

    public String getUnit(){
        return unit ;
    }

    public boolean isNegative(){
        return rate.signum() < 0 ;
    }

    public String format(){
        return  (rate.signum() >= 0 ? "+" : "-") + rate.toPlainString() + unit;
    }

    public int compareTo(SavingsRate other){
        return this.rate.compareTo(other.getRate()) ;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SavingsRate other)) return false;
        return rate.equals(other.rate)
                && unit.equals(other.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rate, unit);
    }

}
