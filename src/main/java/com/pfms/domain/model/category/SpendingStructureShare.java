package com.pfms.domain.model.category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

public class SpendingStructureShare {
    private final BigDecimal share ;
    private final String unit = "%";
    private final String scope ;

    public SpendingStructureShare(BigDecimal share,String scope){
        if(share == null || !(share.compareTo(BigDecimal.valueOf(0)) >= 0 && share.compareTo(BigDecimal.valueOf(100)) <= 0) ){
            throw new BusinessException(ErrorCode.ERR_FIN_002,"Tỉ trọng không hợp lệ") ;
        }
        if(!scope.trim().equalsIgnoreCase("NeedGroup") || !scope.trim().equalsIgnoreCase("Category")){
            throw new BusinessException(ErrorCode.ERR_FIN_002,"Phạm vi phân tích không hợp lệ chỉ nhận NeedGroup / Category") ;
        }
        this.share = share.setScale(2, RoundingMode.HALF_UP) ;
        this.scope = scope.toLowerCase().trim() ;
    }

    public SpendingStructureShare of(BigDecimal itemAmount , BigDecimal totalAmount ,String scope){
        if(totalAmount.signum() > 0) {
            return new SpendingStructureShare((itemAmount.divide(totalAmount)).multiply(BigDecimal.valueOf(100)) , scope);
        }
        return new SpendingStructureShare(BigDecimal.valueOf(0), scope) ;
    }

    public BigDecimal getShare(){
        return share ;
    }

    public String getUnit(){
        return unit ;
    }

    public String getScope(){
        return scope ;
    }

    public boolean isZero(){
        return share.signum() == 0 ;
    }

    public String format(){
        return (share.signum() >= 0 ? "+" : "-") + share.toPlainString() + unit ;
    }
    public int compareTo(SpendingStructureShare other){
        return this.share.compareTo(other.getShare()) ;
    }

    @Override 
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof SpendingStructureShare other)) return false;
        return share.equals(other.share)
                && scope.equals(other.scope);
    }

    @Override 
    public int hashCode(){
        return Objects.hash(share, scope);
    }
}
