package chat.auth;

import chat.handler.DBHandler;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteAuthService implements AuthService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private DBHandler dbHandler;

    public SQLiteAuthService(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public boolean start() {
        logger.log(Level.INFO, "Сервис аутентификации запущен");
        return true;
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        return this.dbHandler.getUserName(login, password);
    }

    @Override
    public void close() { logger.log(Level.INFO, "Сервис аутентификации завершен"); }
}
