package com.learning.command.dto;

public enum CommandOption {
    L("-l"), M("-m"), C("-c"), W("-w");

    private String commandOption;

    CommandOption(String command) {
        this.commandOption = command;
    }

    public String getCommandOption() {
        return commandOption;
    }

    public static CommandOption findCommand(String option) {
        for (CommandOption _c : CommandOption.values()) {
            if (_c.getCommandOption().equals(option)) {
                return _c;
            }
        }
        throw new RuntimeException("Command Option not supported");
    }
}
