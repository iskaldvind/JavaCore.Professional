package chat.auth;

public interface AuthService {

    boolean start();

    String getUsernameByLoginAndPassword(String login, String password);

    void close();
}
