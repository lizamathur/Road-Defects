package dao.roaddefects.defects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.roaddefects.defect.Defect;
import com.roaddefects.defect.PositionAndDefect;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.roaddefects.base.ConnectionHelper;
import dao.roaddefects.base.Globals;

public class DefectsDAO extends ConnectionHelper {

    public List<String> getDefects() {
        connect();
        List<String> defects = new ArrayList<>();
        defects.add("Select Defect");
        String query = "SELECT name FROM defect_type;";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                defects.add(resultSet.getString("name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }
        return defects;
    }

    public List<String> getSubType(String defect) {
        connect();
        List<String> subType = new ArrayList<>();
        String query = "SELECT name from defect_subtype WHERE defect_id = (SELECT id FROM defect_type where name = '" + defect + "');";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next())
                subType.add(resultSet.getString("name"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }
        return subType;
    }

    public List<Defect> getDefectRecords(int user) {
        connect();
        List<Defect> defects = new ArrayList<>();
        Defect defect;
        String query = "SELECT *, sm.name as d_status FROM defectdata dd JOIN status_master sm ON sm.id = dd.status WHERE user_id = " + user + " order by timestamp desc;";
        String defectQuery = "SELECT name from defect_type where id = (?);";
        String crackQuery = "SELECT name from defect_subtype where id = (?);";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int defectType = resultSet.getInt("defect_id");
                int subType = resultSet.getInt("subtype_id");
                double width = resultSet.getDouble("width");
                double length = resultSet.getDouble("length");
                double depth = resultSet.getDouble("depth");
                String severity = resultSet.getString("severity");
                String short_description = resultSet.getString("short_description");
                String description = resultSet.getString("description");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                int user_id = resultSet.getInt("user_id");
                String img_name = resultSet.getString("img_name");
                String status = resultSet.getString("d_status");
                Globals globals = new Globals();
                String uploadUrl =  globals.getUploadUrl() + img_name;
                Bitmap image = null;
                try {
                    URL url = new URL(uploadUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                preparedStatement = connection.prepareStatement(defectQuery);
                preparedStatement.setInt(1, defectType);
                ResultSet defectResultSet = preparedStatement.executeQuery();
                defectResultSet.next();
                String defectName = defectResultSet.getString("name");
                defectResultSet.close();

                String crackName = "";
                preparedStatement = connection.prepareStatement(crackQuery);
                preparedStatement.setInt(1, subType);
                ResultSet crackResultSet = preparedStatement.executeQuery();
                crackResultSet.next();
                crackName = crackResultSet.getString("name");
                crackResultSet.close();

                defect = new Defect();
                defect.setDefect(defectName);
                defect.setSubType(crackName);
                defect.setLength(length);
                defect.setWidth(width);
                defect.setDepth(depth);
                defect.setSeverity(severity);
                defect.setLatitude(latitude);
                defect.setLongitude(longitude);
                defect.setImgName(img_name);
                defect.setImg(image);
                defect.setShortDescription(short_description);
                defect.setDescription(description);
                defect.setUserId(user_id);
                defect.setStatus(status);

                defects.add(defect);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }
        return defects;
    }


    public boolean addDefectRecord(Defect defect) {

        int defectType = 0, subType = 0;

        connect();
        String query = "SELECT id from defect_type where name = '" + defect.getDefect() + "';";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.next();
            defectType = resultSet.getInt(1);

            if (!defect.getSubType().equals("")) {
                query = "SELECT id from defect_subtype where name = '" + defect.getSubType() + "';";
                resultSet = statement.executeQuery(query);
                resultSet.next();
                subType = resultSet.getInt(1);
            }

            query = "INSERT INTO defectdata(defect_id, subtype_id, length, width, depth, latitude, longitude, severity, description, short_description, user_id, img_name) values(?,?,?,?,?,?,?,?,?,?,?,?);";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, defectType);
            preparedStatement.setInt(2, subType);
            preparedStatement.setDouble(3, defect.getLength());
            preparedStatement.setDouble(4, defect.getWidth());
            preparedStatement.setDouble(5, defect.getDepth());
            preparedStatement.setDouble(6, defect.getLatitude());
            preparedStatement.setDouble(7, defect.getLongitude());
            preparedStatement.setString(8, defect.getSeverity());
            preparedStatement.setString(9, defect.getDescription());
            preparedStatement.setString(10, defect.getShortDescription());
            preparedStatement.setLong(11, defect.getUserId());
            preparedStatement.setString(12, defect.getImgName());
            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }
        return false;
    }

    public List<PositionAndDefect> getAllLocations(long user) {
        connect();
        List<PositionAndDefect> positionAndDefects = new ArrayList<>();
        String query = "SELECT d.latitude, d.longitude, d.length, d.width, d.depth, dt.name as d_name, ds.name as dst_name FROM defectdata d INNER JOIN defect_type dt ON d.defect_id = dt.id INNER JOIN defect_subtype ds ON d.subtype_id = ds.id where d.user_id = " + user + " and d.status = (SELECT id FROM status_master WHERE name = 'active')";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                String defect = resultSet.getString("d_name");
                String subType = resultSet.getString("dst_name");
                double length = resultSet.getDouble("length");
                double width = resultSet.getDouble("width");
                double depth = resultSet.getDouble("depth");
                positionAndDefects.add(new PositionAndDefect(latitude, longitude, defect, subType, length, width, depth));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return positionAndDefects;
    }

    public List<PositionAndDefect> getAllActiveLocations() {
        connect();
        List<PositionAndDefect> positionAndDefects = new ArrayList<>();
        String query = "SELECT d.latitude, d.longitude, d.length, d.width, d.depth, dt.name as d_name, ds.name as dst_name FROM defectdata d INNER JOIN defect_type dt ON d.defect_id = dt.id INNER JOIN defect_subtype ds ON d.subtype_id = ds.id where d.status = (SELECT id FROM status_master WHERE name = 'active')" ;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                String defect = resultSet.getString("d_name");
                String subType = resultSet.getString("dst_name");
                double length = resultSet.getDouble("length");
                double width = resultSet.getDouble("width");
                double depth = resultSet.getDouble("depth");
                positionAndDefects.add(new PositionAndDefect(latitude, longitude, defect, subType, length, width, depth));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return positionAndDefects;
    }

    public boolean updateDefect(Defect defect) {
        connect();

        String query = "UPDATE defectdata SET length = " + defect.getLength() + ", width = " + defect.getWidth() + ", depth = " + defect.getDepth() +
                ", description = '" + defect.getDescription() + "', short_description = '" + defect.getShortDescription() + "', severity = '" + defect.getSeverity() + "'" +
                " WHERE img_name = '" + defect.getImgName() + "'";
        try {
            statement = connection.createStatement();
            int count = statement.executeUpdate(query);
            if (count > 0)
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(String imgName) {
        connect();
        String query = "UPDATE defectdata SET status = (SELECT id FROM status_master WHERE name = 'corrected') WHERE img_name = '" + imgName + "'";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public List<Defect> getAllActiveDefects(int active) {
        connect();
        List<Defect> defects = new ArrayList<>();
        Defect defect;
        String query;
        if (active == 1)
            query = "SELECT *, sm.name as d_status FROM defectdata dd JOIN status_master sm ON sm.id = dd.status WHERE sm.name = 'active' order by timestamp desc;";
        else
            query = "SELECT *, sm.name as d_status FROM defectdata dd JOIN status_master sm ON sm.id = dd.status WHERE sm.name = 'corrected' order by timestamp desc;";
        String defectQuery = "SELECT name from defect_type where id = (?);";
        String crackQuery = "SELECT name from defect_subtype where id = (?);";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int defectType = resultSet.getInt("defect_id");
                int subType = resultSet.getInt("subtype_id");
                double width = resultSet.getDouble("width");
                double length = resultSet.getDouble("length");
                double depth = resultSet.getDouble("depth");
                String severity = resultSet.getString("severity");
                String short_description = resultSet.getString("short_description");
                String description = resultSet.getString("description");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                int user_id = resultSet.getInt("user_id");
                String img_name = resultSet.getString("img_name");
                String status = resultSet.getString("d_status");
                Globals globals = new Globals();
                String uploadUrl = globals.getUploadUrl() + img_name;
                Bitmap image = null;
                try {
                    URL url = new URL(uploadUrl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                preparedStatement = connection.prepareStatement(defectQuery);
                preparedStatement.setInt(1, defectType);
                ResultSet defectResultSet = preparedStatement.executeQuery();
                defectResultSet.next();
                String defectName = defectResultSet.getString("name");
                defectResultSet.close();

                String crackName = "";
                preparedStatement = connection.prepareStatement(crackQuery);
                preparedStatement.setInt(1, subType);
                ResultSet crackResultSet = preparedStatement.executeQuery();
                crackResultSet.next();
                crackName = crackResultSet.getString("name");
                crackResultSet.close();

                defect = new Defect();
                defect.setDefect(defectName);
                defect.setSubType(crackName);
                defect.setLength(length);
                defect.setWidth(width);
                defect.setDepth(depth);
                defect.setSeverity(severity);
                defect.setLatitude(latitude);
                defect.setLongitude(longitude);
                defect.setImgName(img_name);
                defect.setImg(image);
                defect.setShortDescription(short_description);
                defect.setDescription(description);
                defect.setUserId(user_id);
                defect.setStatus(status);

                defects.add(defect);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }
        return defects;
    }
}
