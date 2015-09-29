package com.example.lizejun.globalactivity;

import android.content.ComponentName;
import android.net.Uri;

/**
 * Created by lizejun on 2015/09/24.
 */
public class LocalBaseBean {

    private String title;
    private String sub_title;
    private ComponentName componentName;
    private String action;
    private Uri uri;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
