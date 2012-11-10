package ru.fizteh.fivt.bind.test;

import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
@BindingType(MembersToBind.FIELDS)
public final class User {

    private final int id;
    private final UserType userType;
    private final UserName name;
    private final Permissions permissions;
    private final User owner;

    public User(int id, UserType userType, UserName name, Permissions permissions) {
        this(id, userType, name, permissions, null);
    }

    public User(int id, UserType userType, UserName name, Permissions permissions, User owner) {
        this.id = id;
        this.userType = userType;
        this.name = name;
        this.permissions = permissions;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserName getName() {
        return name;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public User getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User other = (User) o;
        return id == other.id
                && name.equals(other.name)
                && !(owner != null ? !owner.equals(other.owner) : other.owner != null)
                && permissions.equals(other.permissions)
                && userType == other.userType;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userType.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + permissions.hashCode();
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
