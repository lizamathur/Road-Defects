package dao.roaddefects.base;

import android.app.Application;

public class Globals extends Application {
    private String ip = "192.168.1.5";
    private String port = "3306";
    private String base_folder = "roaddefect";
    private String images_folder = "uploads";

    public String getIp() {
        return ip;
    }

    public String getPort() { return port; }

    public String getBaseUrl() {
        return "http://" + ip + "/" + base_folder + "/";
    }

    public String getUploadUrl() {
        return getBaseUrl() + images_folder + "/";
    }
}
