package com.simurg.infoboard.ftp;

import com.simurg.infoboard.log.FileLogger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;

import java.io.File;
import java.io.IOException;

public class FtpConnectionManager {
  private  FTPClient ftpClient;
  public static final String TAG= "FtpConnectionManager";
    public FtpConnectionManager(){
     ftpClient= new FTPClient();
    }
    public boolean connect(String host) {
        try {
            FileLogger.log(TAG,"Подключение к серверу: " + host + ":21");
            ftpClient.connect(host);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setParserFactory(new DefaultFTPFileEntryParserFactory());

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                FileLogger.logError(TAG,"Ошибка подключения. Код ответа: " + replyCode);
                disconnect();
                return false;
            }

            FileLogger.log(TAG,"Подключение успешно.");
            return true;

        } catch (IOException e) {
            FileLogger.logError(TAG,"Ошибка подключения: " + e.getMessage());
            return false;
        }
    }
public void setTimeout(int millis){
        ftpClient.setConnectTimeout(millis);
}
    public boolean login(String username, String password) {
        try {
            FileLogger.log(TAG,"Авторизация пользователя: " + username);
            boolean success = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (!success || !FTPReply.isPositiveCompletion(replyCode)) {
                FileLogger.logError(TAG,"Ошибка авторизации. Код ответа: " + replyCode);
                return false;
            }

            FileLogger.log(TAG,"Авторизация успешна.");
            ftpClient.enterLocalPassiveMode();
            return true;

        } catch (IOException e) {
            FileLogger.logError(TAG,"Ошибка авторизации: " + e.getMessage());
            return false;
        }
    }

public void logout(){
        try{
            if(ftpClient.isConnected()){
                ftpClient.logout();
                FileLogger.log(TAG,"Выполнен выход из учетной записи");
            }
        } catch (IOException e) {
            FileLogger.logError(TAG,"Ошибка при выходе из учетной записи "+ e.getMessage());
        }
}
public void disconnect(){
        try{
            if (ftpClient.isConnected()){
                ftpClient.disconnect();
                FileLogger.log(TAG,"Отключение от сервера выполнено");
            }
        }catch (Exception e){
            FileLogger.logError(TAG,"Ошибка при отключении сервера "+e.getMessage());
        }
}

public boolean isConnected(){
      return   ftpClient.isConnected();
}
public FTPClient getFtpClient(){
        return ftpClient;
    }
}
