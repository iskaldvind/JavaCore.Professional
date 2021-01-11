package chat.auth;

import chat.handler.DBHandler;

import java.sql.SQLException;

public class SQLiteAuthService implements AuthService {

    private DBHandler dbHandler;

    public SQLiteAuthService(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public boolean start() {
        System.out.println("Сервис аутентификации запущен");
        return true;
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        return this.dbHandler.getUserName(login, password);
    }

    @Override
    public void close() { System.out.println("Сервис аутентификации завершен"); }
}
