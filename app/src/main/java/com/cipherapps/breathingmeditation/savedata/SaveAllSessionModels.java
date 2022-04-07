package com.cipherapps.breathingmeditation.savedata;

public class SaveAllSessionModels {

    private String Time;
    private String Journal;
    private String Length;

    public SaveAllSessionModels( String time, String length, String journal ) {
        Time = time;
        Journal = journal;
        Length = length;
    }

    public String getTime() {
        return Time;
    }

    public void setTime( String time ) {
        Time = time;
    }

    public String getJournal() {
        return Journal;
    }

    public void setJournal( String journal ) {
        Journal = journal;
    }

    public String getLength() {
        return Length;
    }

    public void setLength( String length ) {
        Length = length;
    }
}
