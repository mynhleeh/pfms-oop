package com.pfms.domain.model.category;

import java.util.* ;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.transaction.TransactionType ;
import status ;


public class NeedGroup{
    private final UUID needGroupId ;
    private String name ;
    private final TransactionType transactionType ;
    private GroupStatus status ;
    private String icon ;
    private String color ;
    private final List<Category> categories ;

    public NeedGroup(UUID needGroupId, String name, TransactionType transactionType, String icon, String color){
        this.needGroupId = needGroupId ;
        this.name = name.trim() ;
        this.transactionType = transactionType ;
        this.status = GroupStatus.ACTIVE ;
        this.icon = icon ;
        this.categories = new ArrayList<>() ;
    }

    public UUID getNeedGroupId(){
        return needGroupId ;
    }
    
    public String getName(){

        return name ;
    }

    public TransactionType getTransactionType(){
        return transactionType ;
    }

    public String getIcon(){
        return icon ;
    }

    public String getColor(){
        return color ;
    }

    public List<Category> getCategories(){
        return Collections.unmodifiableList(categories) ;
    }

    public boolean isActive() {
        return status == CategoryStatus.ACTIVE ;
    }

    public boolean isDeleted(){
        return status == CategoryStatus.DELETED ;
    }

    public void rename(String newName){
        this.name = newName.trim() ;
    }

    public void updateAppearance(String newIcon , String newColor){
        this.icon = newIcon ;
        this.color = newColor;
        
    }

    public void addCategory(Category category){
        if(category.isDeleted() || category.getTransactionType() != this.transactionType){
            throw new BusinessException(ErrorCode.ERR_FEE_003);
            return ;
        }
        this.categories.add(category) ;
    }

    public void removeCategory(Category category){
        if(!this.categories.contains(category)){
            throw new BusinessException(ErrorCode.ERR_NOT_FOUND);
        }
        this.categories.remove(category) ;
    }

    public void markDeleted(){
        this.status = CategoryStatus.DELETED ;
    }

    public void restore(boolean isParentGroupActive){
        this.status = CategoryStatus.ACTIVE ;
    }

}