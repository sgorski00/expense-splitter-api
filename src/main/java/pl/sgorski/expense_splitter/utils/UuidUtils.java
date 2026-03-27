package pl.sgorski.expense_splitter.utils;

import java.util.regex.Pattern;

public final class UuidUtils {

    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isValidUuid(String value) {
        return UUID_REGEX.matcher(value).matches();
    }
}
