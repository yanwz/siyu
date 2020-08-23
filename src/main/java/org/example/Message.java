package org.example;


public abstract class Message {

    private String fromId;

    public Message(String fromId) {
        if(fromId == null){
            throw new NullPointerException();
        }
        this.fromId = fromId;
    }

    public String getFromId() {
        return fromId;
    }

}
