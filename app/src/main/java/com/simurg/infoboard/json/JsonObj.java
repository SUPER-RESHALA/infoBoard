package com.simurg.infoboard.json;

import java.io.File;

public class JsonObj {
    protected volatile String name;
    protected volatile File file;

    public JsonObj(String name) {
        this.name = name;
    }

    public JsonObj(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public JsonObj(File file) {
        this.file = file;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized File getFile() {
        return file;
    }

    public synchronized void setFile(File file) {
        this.file = file;
    }
}
