package com.simurg.infoboard.log;

import android.content.Context;

import com.simurg.infoboard.config.Config;
import com.simurg.infoboard.ftp.FtpConnectionManager;
import com.simurg.infoboard.ftp.FtpFileManager;

import java.io.IOException;

public class LogsToFtp {
    public static void sendLogsToFtp(Config config, String logDirectoryPath, Context context, String prefsName){
        FtpConnectionManager ftpConnectionManager = new FtpConnectionManager();
        FtpFileManager ftpFileManager= new FtpFileManager(ftpConnectionManager.getFtpClient());
        try{
            ftpConnectionManager.connect(config.getHost());
            ftpConnectionManager.login(config.getUserName(),config.getPassword());
            ftpFileManager.ensureAndChangeToDirectory(ftpFileManager,logDirectoryPath);
            boolean success=false;
            if (FileLogger.isLogExist()){
                 success= ftpFileManager.uploadFile(FileLogger.getLogFilePath());
            }else{ FileLogger.init(context,prefsName);}
            if (success){FileLogger.checkAndDeleteLog(context,prefsName);}
        }catch (IOException e){
            FileLogger.logError("sendLogsToFtp", "Exception when send logs "+ e.getMessage());
        }finally {
            if (ftpConnectionManager.isConnected()){
                ftpConnectionManager.logout();
                ftpConnectionManager.disconnect();
            }
            }
    }
}
