package com.learning;

import com.learning.command.Commander;
import com.learning.command.dto.CommandAttribute;
import com.learning.command.dto.CommandOption;
import com.learning.factory.ArgumentParserFactory;
import com.learning.utility.FileUtility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<CommandAttribute> commandAttributes = getCommandAttributes(args);

        CommandAttribute commandAttribute = commandAttributes.get(0);

        Commander commander = commandAttribute.getCommander();
        CommandOption[] commandOptions = commandAttribute.getCommandOptions();
        Object content = commandAttribute.getContent();

        commander.execute(commandOptions, content);
    }

    private static List<CommandAttribute> getCommandAttributes(String[] args) {
        FileUtility fileUtility = new FileUtility();
        ArgumentParserFactory argumentParserFactory = new ArgumentParserFactory(fileUtility);

        String commandStr = argumentParserFactory.joinArguments(args);

        List<String> commandStrings = argumentParserFactory.collectCommandsString(commandStr);
        return argumentParserFactory.getCommandAttributeList(commandStrings);
    }
}