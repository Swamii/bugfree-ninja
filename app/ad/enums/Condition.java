package ad.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swami on 14/05/14.
 */
public enum Condition {
    SUPER_NICE("sn", "Super nice"),
    MEDIUM_NICE("mn", "Medium nice"),
    NOT_SO_NICE("nsn", "Not so nice"),
    NOT_NICE("nn", "Not nice");

    private String id;
    private String displayName;

    public static Map<String, Condition> nameMapping;

    private Condition(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static void initMapping() {
        nameMapping = new HashMap<String, Condition>();
        for (Condition c : values()) {
            nameMapping.put(c.getDisplayName(), c);
        }
    }

    public static Condition fromDisplayName(String name) {
        if (nameMapping == null) {
            initMapping();
        }
        return nameMapping.get(name);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Object[] choices() {
        if (nameMapping == null) {
            initMapping();
        }
        return nameMapping.keySet().toArray();
    }
}
