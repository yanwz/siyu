package org.example;

public class SyncMessage extends Message {

    private Long limit;

    public SyncMessage(String fromId,Long limit) {
        super(fromId);
        if(limit == null){
            throw new NullPointerException();
        }
        this.limit = limit;
    }

    public Long getLimit() {
        return limit;
    }
}
