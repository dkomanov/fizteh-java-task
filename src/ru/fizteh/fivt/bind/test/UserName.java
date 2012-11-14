package ru.fizteh.fivt.bind.test;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public final class UserName {

    private final String firstName;
    private final String lastName;

    public UserName(String firstName, String lastName) {
        if (firstName == null) {
            throw new IllegalArgumentException("firstName is null");
        }
        if (lastName == null) {
            throw new IllegalArgumentException("lastName is null");
        }

        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserName other = (UserName) o;
        return firstName.equals(other.firstName)
                && lastName.equals(other.lastName);

    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
