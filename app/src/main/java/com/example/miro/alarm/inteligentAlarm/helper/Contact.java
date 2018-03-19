package com.example.miro.alarm.inteligentAlarm.helper;

import com.example.miro.alarm.inteligentAlarm.enums.Permission;

import java.io.Serializable;

public class Contact implements Serializable {

    private String name;
    private String phoneNumber;
    private Permission permission;
    private boolean hasApp;

    public Contact(final String name, final String phoneNumber, final boolean hasApp,
                   final Permission permission) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.hasApp = hasApp;
        this.permission = permission;

    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }


    public String getId() {
        return phoneNumber;
    }


    public boolean getHasApp() {
        return hasApp;
    }

}

