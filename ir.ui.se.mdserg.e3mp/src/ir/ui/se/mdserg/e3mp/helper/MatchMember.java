package ir.ui.se.mdserg.e3mp.helper;

import java.io.Serializable;

public class MatchMember implements Serializable{
    private String BaseParent; 
    private String BaseText;  
    private String NewVersionParent;
    private String NewVersionText; 
    private int isSelected;
    public MatchMember(){}
    
    public MatchMember(String LeftParent, String LeftText, String RightParent, String RightText, int isSelected){
    	this.BaseParent = LeftParent ; 
    	this.BaseText = LeftText ; 
    	this.NewVersionParent = RightParent ; 
    	this.NewVersionText = RightText ; 
    	this.isSelected = isSelected; 
    }
    
    public String getLeftParent(){
    	return BaseParent ; 
    }
    public void setLeftParent(String LeftParent){
    	this.BaseParent = LeftParent ; 
    }
    
    public String getLeftText(){
    	return BaseText ; 
    }
    public void setLeftText(String LeftText){
    	this.BaseParent = LeftText ; 
    }
    
    public String getRightParent(){
    	return NewVersionParent ; 
    }
    public void setRightParent(String RightParent){
    	this.NewVersionParent = RightParent ; 
    }
    
    public String getRightText(){
    	return NewVersionText ; 
    }
    public void setRightText(String RightText){
    	this.NewVersionParent = RightText ; 
    }
    
    public int getisSelected(){
    	return isSelected ; 
    }
    public void setisSelected(int isSelected){
    	this.isSelected = isSelected ; 
    }
}
