package ru.fizteh.fivt.bind.test;

import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
@BindingType(MembersToBind.GETTERS_AND_SETTERS)
public final class Permissions {

    private final Map<String, String> map = new HashMap<String, String>();

    public Permissions() {
        setRoot(false);
        setQuota(10);
    }

    public boolean isRoot() {
        return Boolean.valueOf(getOrElse("root", Boolean.FALSE.toString()));
    }

    public void setRoot(boolean value) {
        put("root", Boolean.valueOf(value).toString());
    }

    public int getQuota() {
        return Integer.parseInt(getOrElse("quota", "10"));
    }

    public void setQuota(int value) {
        put("quota", Integer.toString(value));
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && getClass() == o.getClass() && map.equals(((Permissions) o).map));
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    private void put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }

        map.put(key, value);
    }

    private String getOrElse(String key, String defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue is null");
        }

        String value = map.get(key);
        return value == null
                ? defaultValue
                : value;
    }
}
