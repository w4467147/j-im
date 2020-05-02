package org.jim.core.packets;

public enum UserStatusType {
    /**
     * <pre>
     * 在线
     * </pre>
     *
     * <code>ONLINE = 0;</code>
     */
    ONLINE(0),
    /**
     * <pre>
     *离线
     * </pre>
     *
     * <code>OFFLINE = 1;</code>
     */
    OFFLINE(1),
    /**
     * <pre>
     * ALL所有(在线+离线)
     * </pre>
     *
     * <code>ALL = 2;</code>
     */
    ALL(2);


    public final int getNumber() {
        return value;
    }

    public static UserStatusType valueOf(int value) {
        return forNumber(value);
    }

    public static UserStatusType forNumber(int value) {
        switch (value) {
            case 0: return ONLINE;
            case 1: return OFFLINE;
            case 2: return ALL;
            default: return null;
        }
    }
    private final int value;

    UserStatusType(int value) {
        this.value = value;
    }
}
