package com.example.mdb.enums;

public enum ScreenType {

    TWO_D("2D"),
    THREE_D("3D"),
    FOUR_DX("4DX");

    private final String displayName;

    ScreenType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}