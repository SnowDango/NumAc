package com.snowdango.numac.ListFormat;

import android.graphics.drawable.Drawable;

public class AppListFormat {
    private long id;
    private Drawable appIcon;
    private String appName;
    private String appCommand;
    private String appClassName;
    private String appPackageName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName(){
        return appName;
    }

    public void setAppName(String appName){
        this.appName=appName;
    }

    public String getAppCommand(){
        return appCommand;
    }

    public void setAppCommand(String appCommand){
        this.appCommand = appCommand;
    }

    public void setAppClassName(String appClassName){
        this.appClassName = appClassName;
    }

    public String getAppClassName(){
        return appClassName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

}