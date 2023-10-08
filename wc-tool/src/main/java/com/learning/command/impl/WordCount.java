package com.learning.command.impl;

import com.learning.command.Commander;
import com.learning.command.dto.CommandOption;
import com.learning.utility.StringUtility;

import java.nio.charset.StandardCharsets;

public class WordCount implements Commander {

    @Override
    public void execute(CommandOption[] commandOptions, Object content) {
        byte[] bytes = (byte[]) content;
        if (commandOptions.length == 0) {
            countLines(bytes);
            countWords(bytes);
            fileBytes(bytes);
        } else {
            for (CommandOption commandOption : commandOptions) {
                if (commandOption.equals(CommandOption.C)) {
                    fileBytes(bytes);
                } else if (commandOption.equals(CommandOption.L)) {
                    countLines(bytes);
                } else if (commandOption.equals(CommandOption.W)) {
                    countWords(bytes);
                } else if (commandOption.equals(CommandOption.M)) {
                    countCharacter(bytes);
                } else {
                    throw new RuntimeException("Command option not supported.");
                }
            }
        }
    }

    public void countCharacter(byte[] bytes) {
        String content = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("Total Character in files " + content.length());
    }

    public void countWords(byte[] bytes) {
        int count = 0;
        String s = StringUtility.decodeString(bytes, StandardCharsets.UTF_8);

        String[] words = s.split("\\s");
        for (String p : words) {
            if (!p.equals("")) {
                count++;
            }
        }

        System.out.println("Total Words in files " + count);
    }

    public void countLines(byte[] bytes) {
        String s = StringUtility.decodeString(bytes, StandardCharsets.UTF_8);
        int lines = StringUtility.numberOfLines(s);
        System.out.println("Total Lines in files " + lines);
    }

    public void fileBytes(byte[] bytes) {
        System.out.println("Total Bytes of files is bytes[] " + bytes.length);
    }
}
