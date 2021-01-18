package chat.handler;

import java.sql.*;
import java.util.ArrayList;

public class DBHandler implements AutoCloseable {

    private static DBHandler instance;
    private static Connection connection;

    public static DBHandler getInstance() {
        if (instance == null) {
            loadDriverAndOpenConnection();
            instance = new DBHandler();
        }
        return instance;
    }

    private static void loadDriverAndOpenConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ChatServer/ChatDB.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getUsersList() {
        ArrayList<String> usersList = new ArrayList<>();
        try {
            String query = "SELECT name FROM user";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) usersList.add(resultSet.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public String getUserName(String login, String password) {
        String name = null;
        try {
            String query = "SELECT name FROM user WHERE (login = ? AND password = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) name = resultSet.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public boolean changeUserName(String login, String name) {
        try {
            String query = "UPDATE user SET name = ? WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, login);
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
