package com.simurg.infoboard.item;

import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;

public abstract class MediaItem {
    protected String name;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_TEXT = 3;
    public static final String nameStr="name";
    public static final String durationStr="duration";
    protected File file;
    MediaItem(File item){
 this.file= item;
}
MediaItem(String name){
        this.name=name;
}
MediaItem(File file,String name){
        this.file=file;
        this.name=name;
}

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public abstract int getType();
    public abstract void play(MediaPlayerManager mediaPlayerManager);
}
