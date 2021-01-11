package clientserver.commands;

import java.io.Serializable;

public class UpdateErrorCommandData implements Serializable {

    private final String errorMessage;

    public UpdateErrorCommandData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
