package dao.roaddefects.users;

import android.util.Log;

import androidx.annotation.NonNull;

import com.roaddefects.users.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.roaddefects.base.ConnectionHelper;

public class UserDAO extends ConnectionHelper {

    public boolean insertUser(User user){

        int count = 0;
        boolean success = false;
        connect();

        String query = "INSERT INTO users(mobile, name, email, password) values(" + user.getMobile() + ",'" +
                user.getName() +"', '" + user.getEmail() + "', '" + user.getPass() + "');";
        Log.v("My Query", query);
        try {
            statement = connection.createStatement();
            count = statement.executeUpdate(query);
            if(count > 0)
                success = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
        }

        return success;
    }

    public User fetchUser(String email, String password){

        User user = null;
        connect();
        String query = "SELECT * FROM users where email = '" + email + "' and password = '" + password + "';";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(email);
                user.setMobile(resultSet.getLong("mobile"));
                user.setPass("");
                user.setRole(resultSet.getString("role"));
                user.setStatus(resultSet.getString("status"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
        }
        return user;
    }

    public String fetchStatus(long user_id) {
        connect();
        String query = "SELECT status FROM users where id = " + user_id;
        String status = "";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                status = resultSet.getString("status");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
        }
        return status;
    }

    public List<User> fetchUsers(String status) {
        connect();
        String query = "SELECT id, name, email, mobile, status FROM users WHERE status = '" + status + "' and role = 'normal'";
        List<User> newUsers = new ArrayList<>();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                long mobile = resultSet.getLong("mobile");
                newUsers.add(new User(id, name, email, mobile, status));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
        }
        return newUsers;
    }

    public boolean approveUsers(List<Integer> selected) {
        connect();
        String users = selected.toString();
        users = users.substring(1, users.length() - 1);

        String query = "UPDATE users SET status = 'active' WHERE id in (" + users + ")";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
