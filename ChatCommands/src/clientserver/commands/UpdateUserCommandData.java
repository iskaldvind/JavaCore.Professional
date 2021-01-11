package clientserver.commands;

import java.io.Serializable;
import java.util.List;

public class UpdateUserCommandData implements Serializable {

    private final String login;
    private final String newName;

    public UpdateUserCommandData(String login, String newName) {
        this.login = login;
        this.newName = newName;
    }

    public String getLogin() {
        return this.login;
    }

    public String getNewName() { return this.newName; }
}
