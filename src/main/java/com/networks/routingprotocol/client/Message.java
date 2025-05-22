package com.networks.routingprotocol.client;

import java.io.Serializable;

public class Message implements Serializable{
    private int id;
    private String message;

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
    }
    
    public String getContent() {return message;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public void setMessage(String message) {this.message = message;}
}
