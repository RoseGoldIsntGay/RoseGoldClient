package rosegoldclient.events;

import java.time.LocalDateTime;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MillisecondEvent extends Event {
    public LocalDateTime dateTime;

    public MillisecondEvent() {
        dateTime = LocalDateTime.now();
    }
}
