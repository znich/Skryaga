package by.laguta.skryaga.service.impl;

import java.util.regex.Matcher;

public class BaseParser {

    protected static final String DOUBLE_REGEXP = "[-\\+]?\\d+(?:\\s+\\d+)?(?:\\.\\d+|,\\d+)?";

    protected static final String DATE_REGEXP =
            "(?:0[1-9]|[12][0-9]|3[01])[- /.//](?:0[1-9]|1[012])(?:[- /.](?:19|20)?\\d\\d)?";

    protected static final String TIME_REGEXP = "(?:[0-1]\\d|2[0-3])(?::[0-5]\\d)*";


    protected Double parseDouble(Matcher matcher, int group) {
        String doubleString = matcher.group(group);
        if (doubleString != null) {
            doubleString = doubleString.replace(",", ".").replace(" ", "");
            return Double.parseDouble(doubleString);
        } else {
            return null;
        }
    }
}
