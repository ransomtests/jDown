package com.personal.projects.jdown.utils;

import java.util.HashMap;
import java.util.Map;

public class CommandLineParser {
    public static Map<String, String> parse(String[] args) {

        HashMap<String, String> parsedArguments = new HashMap<>();

        if (args.length > 1) {
            parsedArguments.put("outputDirectory", args[1]);
            parsedArguments.put("url", args[0]);
        } else {
            parsedArguments.put("url", args[0]);
        }
        return parsedArguments;
    }
}
