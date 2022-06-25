package rosegoldclient.events;

import java.time.LocalDateTime;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SecondEvent extends Event {
    public LocalDateTime dateTime;

    public SecondEvent() {
        dateTime = LocalDateTime.now();
    }
}
