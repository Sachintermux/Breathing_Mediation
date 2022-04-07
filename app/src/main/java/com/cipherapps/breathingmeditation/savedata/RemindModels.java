package com.cipherapps.breathingmeditation.savedata;

public class RemindModels {

    private String name;
    private int hour;
    private int minute;
    private String shortDescription;

    public RemindModels( String name, String shortDescription, int hour, int minute ) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.shortDescription = shortDescription;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour( int hour ) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute( int minute ) {
        this.minute = minute;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription( String shortDescription ) {
        this.shortDescription = shortDescription;
    }


}
