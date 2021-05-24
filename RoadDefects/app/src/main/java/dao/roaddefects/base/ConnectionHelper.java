package dao.roaddefects.base;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionHelper {
    Globals globals = new Globals();
    String connectionURL = "jdbc:mysql://"+ globals.getIp() + ":" + globals.getPort() + "/roadalert";
    String user = "root";
    String password = "root";

    protected Connection connection = null;
    protected Statement statement = null;
    protected PreparedStatement preparedStatement = null;
    protected ResultSet resultSet = null;

    protected void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionURL, user, password);
            if(connection != null)
                Log.v("Connection", "Success");
            else
                Log.v("Connection", "Null");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}