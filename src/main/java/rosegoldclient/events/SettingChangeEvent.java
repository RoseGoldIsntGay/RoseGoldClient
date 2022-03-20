package rosegoldclient.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import rosegoldclient.config.settings.Setting;

public class SettingChangeEvent extends Event {
    public Setting setting;
    public Object oldValue;
    public Object newValue;

    public SettingChangeEvent(Setting setting, Object oldValue, Object newValue) {
        this.setting = setting;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
