package com.liuwanwan.accountbook.model;

public class MessageEvent{
    private boolean message;
    public MessageEvent(boolean message){
        this.message=message;
    }
    public boolean getMessage(){
        return message;
    }
    public void setMessage(boolean message){
        this.message=message;
    }
}