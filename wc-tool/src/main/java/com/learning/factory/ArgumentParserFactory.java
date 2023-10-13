package com.learning.factory;

import com.learning.command.Commander;
import com.learning.command.dto.CommandAttribute;
import com.learning.command.dto.CommandOption;
import com.learning.command.impl.WordCount;
import com.learning.utility.FileUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArgumentParserFactory {

    FileUtility fileUtility;

    public ArgumentParserFactory(FileUtility fileUtility) {
        this.fileUtility = fileUtility;
    }

    public Commander getCommander(String arg) {
        return findCommand(arg);
    }

    public List<CommandAttribute> getCommandAttributeList(List<String> commandList) {
        List<CommandAttribute> commanders = new ArrayList<>();

        for (String command : commandList) {
            String[] breakCommand = command.split("\\s");

            Commander commander = findCommand(breakCommand[0]);
            CommandOption[] options = getOptions(breakCommand);
            CommandAttribute e = new CommandAttribute(commander, options);

            String lastAttribute = breakCommand[breakCommand.length - 1];

            if (lastAttribute.contains(".txt")) {
                byte[] bytes = fileUtility.readBytes(lastAttribute);
                e.setContent(bytes);
            } else {
                // stdinput is used.
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                StringBuilder output = new StringBuilder();
                try {
                    // Read characters from the standard input
                    int charCode;
                    while ((charCode = br.read()) != -1) {
                        // Convert the character to a byte and append to the StringBuilder
                        byte[] bytes = Character.toString((char) charCode).getBytes(StandardCharsets.UTF_8);
                        output.append(new String(bytes, StandardCharsets.UTF_8));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                e.setContent(output.toString().getBytes(StandardCharsets.UTF_8));

                /*
                Leave it for learning.
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[32 * 1024];

                int bytesRead;
                while (true) {
                    try {
                        if (!((bytesRead = System.in.read(buffer)) > 0)) break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    baos.write(buffer, 0, bytesRead);
                }
                byte[] bytes = baos.toByteArray();
                e.setContent(bytes);
                */
            }
            commanders.add(e);
        }
        return commanders;
    }

    private CommandOption[] getOptions(String[] breakCommand) {
        List<CommandOption> commandOptions = new ArrayList<>();
        for (String s : breakCommand) {
            if (s.startsWith("-")) {
                commandOptions.add(CommandOption.findCommand(s));
            }
        }

        int idx = 0;
        CommandOption[] commandOptionArray = new CommandOption[commandOptions.size()];

        for (CommandOption _c : commandOptions) {
            commandOptionArray[idx] = _c;
            idx++;
        }

        return commandOptionArray;
    }

    private Commander findCommand(String command) {
        if (command.equals("ccwc")) {
            return new WordCount();
        }
        throw new RuntimeException("Command Not Found!");
    }

    public String joinArguments(String[] arg) {
        return String.join(" ", arg);
    }

    public List<String> collectCommandsString(String command) {
        return Arrays.stream(command.split("\\|")).collect(Collectors.toList());
    }
}
