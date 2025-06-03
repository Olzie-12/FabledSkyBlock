package com.songoda.skyblock.upgrade;

public class Upgrade {
    private double cost;
    private int value;
    private boolean enabled = true;

    public Upgrade(double cost) {
        this.cost = cost;
    }

    public Upgrade(double cost, int value) {
        this.cost = cost;
        this.value = value;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public enum Type {
        CROP("Crop"),
        SPAWNER("Spawner"),
        FLY("Fly"),
        DROPS("Drops"),
        SIZE("Size"),
        SPEED("Speed"),
        JUMP("Jump"),
        MEMBERS("Members");

        private final String friendlyName;

        Type(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName() {
            return this.friendlyName;
        }
    }
}
