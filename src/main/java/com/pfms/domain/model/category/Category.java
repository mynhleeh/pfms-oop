package com.pfms.domain.model.category;

import java.util.UUID;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.exception.ErrorCode ;


public class Category {
    private final UUID categoryId ;
    private String name ;
    private UUID needGroupId ;
    private final TransactionType transactionType ;
    private CategoryStatus status;
    private String icon ;
    private String color ; 

    public Category(UUID categoryId, String name, UUID needGroupId, TransactionType transactionType, String icon, String color){
        this.categoryId = UUID.randomUUID() ;
        this.name = name ;
        this.needGroupId = needGroupId ;
        this.transactionType = transactionType ;
        this.status = CategoryStatus.ACTIVE ;
        this.icon = icon ;
        this.color = color ;
    }

    public UUID getCategoryId() {
        return categoryId ;
    }

    public String getName() {
        return name ;
    }

    public UUID getNeedGroupId(){
        return needGroupId ;
    }

    public TransactionType getTransactionType(){
        return transactionType ;
    }

    public CategoryStatus getStatus(){
        return status ;
    }

    public String getIcon(){
        return icon ;
    }

    public String getColor(){
        return color ;
    }

    public boolean isActive() {
        return status == CategoryStatus.ACTIVE ;
    }

    public boolean isDeleted(){
        return status == CategoryStatus.DELETED ;
    }

    public void rename(String newName){
        if(newName == null || newName.trim().isEmpty() || newName.trim().length() > 50){
            throw new BusinessException(ErrorCode.ERR_NAM_002) ;
            return ;
        }
        this.name = newName.trim() ;
    }

    public void changeNeedGroup(UUID newNeedGroupId,TransactionType targetGroupType){
        if(newNeedGroupId.isDeleted() || targetGroupType != this.transactionType){
            throw new BusinessException(ErrorCode.ERR_CAT_003) ;
            return ;
        }
        this.needGroupId = newNeedGroupId ;
    }

    public void updateAppearance(String newIcon , String newColor){
        this.icon = newIcon ;
        this.color = newColor ;
    }

    public void markDeleted(){
        this.status = CategoryStatus.DELETED ;
    }

    public void restore(boolean isParentGroupActive){
        if(!isParentGroupActive){
            throw new BusinessException(ErrorCode.ERR-CAT-004) ;
            return ;
        }
        this.status = CategoryStatus.ACTIVE ;
    }

}
