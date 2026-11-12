package com.pfms.domain.model.category;

import java.util.UUID;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.model.common.EntityName;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.exception.ErrorCode ;


public class Category {
    private final UUID categoryId ;
    private EntityName name ;
    private UUID needGroupId ;
    private final TransactionType transactionType ;
    private EntityStatus status;
    private String icon ;
    private String color ; 

    public Category(UUID categoryId, String name, UUID needGroupId, TransactionType transactionType, String icon, String color){
        this.categoryId = UUID.randomUUID() ;
        this.name = EntityName.of(name,"CategoryName") ;
        this.needGroupId = needGroupId ;
        this.transactionType = transactionType ;
        this.status = EntityStatus.ACTIVE ;
        this.icon = icon ;
        this.color = color ;
    }

    public UUID getCategoryId() {
        return categoryId ;
    }

    public String getName() {
        return name.getValue() ;
    }

    public UUID getNeedGroupId(){
        return needGroupId ;
    }

    public TransactionType getTransactionType(){
        return transactionType ;
    }

    public EntityStatus getStatus(){
        return status ;
    }

    public String getIcon(){
        return icon ;
    }

    public String getColor(){
        return color ;
    }

    public boolean isActive() {
        return status.isActive() ;
    }

    public boolean isDeleted(){
        return status.isDeleted() ;
    }

    public void rename(String newName){
        this.name = EntityName.of(newName,"CategoryName") ;
    }

    public void changeNeedGroup(UUID newNeedGroupId,TransactionType targetGroupType){
        // region FindBYID
        if(findID(newNeedGroupId).isDeleted() || targetGroupType != this.transactionType){
            throw new BusinessException(ErrorCode.ERR_CAT_003) ;
        }
        this.needGroupId = newNeedGroupId ;
    }

    public void updateAppearance(String newIcon , String newColor){
        this.icon = newIcon ;
        this.color = newColor ;
    }

    public void markDeleted(){
        this.status = EntityStatus.DELETED ;
    }

    public void restore(boolean isParentGroupActive){
        if(!isParentGroupActive){
            throw new BusinessException(ErrorCode.ERR_CAT_004) ;
        }
        this.status = EntityStatus.ACTIVE ;
    }

}
