package seedu.address.model.password;

import static java.util.Objects.requireNonNull;

import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Password's password value in the password book.
 */
public class PasswordValue {
    public static final String MESSAGE_CONSTRAINTS =
            "Password can contain any strings and "
            + "adhere to the following constrains: \n"
            + "1) Should not have spaces at the start or end\n"
            + "2) Be between 2 characters to 25 characters long\n"
            + "Password is case-sensitive";

    private static final String VALIDATION_REGEX = "^(?![ ])([^\\s].{1,24})(?<![ ])$";

    public final String value;

    public PasswordValue(String passwordValue) {
        requireNonNull(passwordValue);
        checkArgument(isValidPasswordValue(passwordValue), MESSAGE_CONSTRAINTS);
        value = passwordValue;
    }

    /**
     * Returns true if a given string is a valid username.
     */
    public static boolean isValidPasswordValue(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public String getEncryptedPasswordValue() {
        return StringUtil.passwordToAsterix(value);
    }

    public String getNonEncryptedPasswordValue() {
        return value;
    }

    @Override
    public String toString() {
        return getEncryptedPasswordValue();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PasswordValue // instanceof handles nulls
                && value.equals(((PasswordValue) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
