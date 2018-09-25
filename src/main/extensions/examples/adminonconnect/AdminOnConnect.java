package main.extensions.examples.adminonconnect;

import main.extensions.Extension;
import main.extensions.ExtensionInfo;
import main.protocol.HMessage;
import main.protocol.HPacket;

/**
 * Created by Jonas on 26/06/18.
 */



@ExtensionInfo(
        Title = "Always admin!",
        Description = "Gives you admin permission on connect",
        Version = "1.0",
        Author = "sirjonasxx"
)
public class AdminOnConnect extends Extension {

    public static void main(String[] args) {
        new AdminOnConnect(args);
    }
    public AdminOnConnect(String[] args) {
        super(args);
    }

    private boolean done = true;

    protected void init() {
        intercept(HMessage.Side.TOCLIENT, message -> {
            if (!done) {
                HPacket packet = message.getPacket();
                if (packet.length() == 11) {
                    if (packet.readByte(14) == 0 || packet.readByte(14) == 1) {
                        packet.replaceInt(6, 7);
                        packet.replaceInt(10, 7);
                        packet.replaceBoolean(14, true);

                        done = true;
                    }
                }
            }
        });
    }

    protected void onStartConnection() {
        done = false;
    }
}
