package rosegoldclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class KeybindEnabledEvent extends Event {
    public int keyCode;

    public KeybindEnabledEvent(int keyCode) {
        this.keyCode = keyCode;
    }
}
