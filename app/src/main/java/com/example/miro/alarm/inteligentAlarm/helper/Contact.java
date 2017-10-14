package com.example.miro.alarm.inteligentAlarm.helper;

import java.io.Serializable;

public class Contact implements Serializable{


    private String name;


    private int contactId;


    private boolean hasApp;


    public Contact(final String name, final int contactId, final boolean hasApp) {
        this.name = name;
        this.contactId =contactId;
        this.hasApp = hasApp;
    }


    public String getName() {
        // TODO implement me
        return name;
    }


    public int getId() {
        // TODO implement me
        return contactId;
    }


    public boolean getHasApp() {
        // TODO implement me
        return hasApp;
    }


    public void appInstalled() {
        // TODO implement me
    }

}

