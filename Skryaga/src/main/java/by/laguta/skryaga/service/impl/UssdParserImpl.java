package by.laguta.skryaga.service.impl;

import by.laguta.skryaga.service.UssdParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UssdParserImpl extends BaseParser implements UssdParser {

    private static final String BALANCE_REGEXP = ".*Dostupno:\\s+(" + DOUBLE_REGEXP + ")\\s+(\\S{3})";

    @Override
    public Double parseBalanceAmount(String message) {
        Matcher matcher = Pattern.compile(BALANCE_REGEXP).matcher(message);
        if (matcher.find()) {
            return parseDouble(matcher, 1);
        }
        return null;
    }
}
