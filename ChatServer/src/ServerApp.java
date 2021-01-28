import chat.MyServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApp {

    private static final int DEFAULT_PORT = 8189;
    private static Logger logger = Logger.getLogger("ServerApp");

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            new MyServer(port).start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getStackTrace().toString());
            System.exit(1);
        }
    }
}
