package com.example.bilbiophile.provider;

public class IdProvider {
    private static int sTaskId = 0;

    public static int generateTaskId() {
        sTaskId++;
        return sTaskId;
    }
}
