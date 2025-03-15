package com.simurg.infoboard.item;

import com.simurg.infoboard.player.MediaPlayerManager;

import java.io.File;

public abstract class MediaItem {
    //protected String name;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_TEXT = 3;
   protected File file;
    MediaItem(File item){
 this.file= item;
}
    public abstract int getType();
    public abstract void play(MediaPlayerManager mediaPlayerManager);
}
