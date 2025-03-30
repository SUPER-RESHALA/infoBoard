package com.simurg.infoboard.config;

import com.simurg.infoboard.json.JSONHandler;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {
    protected static final String HOST_KEY = "host";
    protected static final String USERNAME_KEY = "userName";
    protected static final String PASSWORD_KEY = "password";
    protected static final String MEDIA_DIR_NAME_KEY = "mediaDirName";
    protected static final String JSON_NAME_KEY = "jsonName";
    protected static final String JSON_FILE_KEY = "jsonFile";
    public static final String[] CONFIG_KEYS = {
            "host",
            "userName",
            "password",
            "mediaDirName",
            "jsonName",
            "id"

    };
    protected String host;
    protected String userName;
    protected String password;
    protected String mediaDirName;
    protected String jsonName;
    protected String id;
    protected File jsonFile;

    public Config(String host, String userName, String password, String mediaDirName, String jsonName, String id) {
        this.host = host;
        this.userName = userName;
        this.password = password;
        this.mediaDirName = mediaDirName;
        this.jsonName = jsonName;
        this.id=id;
    }

    public Config(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    public File getJsonFile() {
        return jsonFile;
    }

    public String getHost() {
        return host;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getMediaDirName() {
        return mediaDirName;
    }

    public String getId() {
        return id;
    }

    public String getJsonName() {
        return jsonName;
    }
    public Map<String, String> getAllConfigValues() throws IOException {
        return JSONHandler.readConfigJson(jsonFile);
    }
    public boolean setupConfig(Map<String, String> values) {
        for (String key : CONFIG_KEYS) {
            if (values.containsKey(key) && values.get(key) != null) {
                String value = values.get(key);
                switch (key) {
                    case "host":
                        this.host = value;
                        break;
                    case "userName":
                        this.userName = value;
                        break;
                    case "password":
                        this.password = value;
                        break;
                    case "mediaDirName":
                        this.mediaDirName = value;
                        break;
                    case "jsonName":
                        this.jsonName = value;
                        break;
                    case "id":
                        this.id = value;
                        break;
                    default:
                        return false;

                }
            }else {
                return false;
            }
        }
        return true;
    }

}
