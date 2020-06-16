package com.snowdango.numac.ListFormat;

public class SharpCommandListFormat {

    private String command;
    private String text;
    private Runnable runnable;
    private int lateTime,updateLateTime;

    public SharpCommandListFormat(String command, String text, Runnable runnable, int lateTime, int updateLateTime) {
        this.command = command;
        this.text = text;
        this.runnable = runnable;
        this.lateTime = lateTime;
        this.updateLateTime = updateLateTime;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public int getLateTime() {
        return lateTime;
    }

    public void setLateTime(int lateTime) {
        this.lateTime = lateTime;
    }

    public int getUpdateLateTime() {
        return updateLateTime;
    }

    public void setUpdateLateTime(int updateLateTime) {
        this.updateLateTime = updateLateTime;
    }
}
