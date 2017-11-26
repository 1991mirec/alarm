package com.example.miro.alarm.inteligentAlarm.helper;

import java.io.Serializable;

public class Contact implements Serializable {

    private String name;
    private String phoneNumber;
    private boolean hasApp;

    public Contact(final String name, final String phoneNumber, final boolean hasApp) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.hasApp = hasApp;
    }

    public String getName() {
        // TODO implement me
        return name;
    }


    public String getId() {
        // TODO implement me
        return phoneNumber;
    }


    public boolean getHasApp() {
        // TODO implement me
        return hasApp;
    }

}

