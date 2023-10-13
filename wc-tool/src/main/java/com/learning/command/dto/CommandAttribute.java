package com.learning.command.dto;

import com.learning.command.Commander;

public class CommandAttribute {
    private Commander commander;
    private CommandOption[] commandOptions;
    private Object content;

    public CommandAttribute(Commander commander) {
        this.commander = commander;
    }

    public CommandAttribute(Commander commander, CommandOption[] commandOptions, Object content) {
        this.commander = commander;
        this.commandOptions = commandOptions;
        this.content = content;
    }

    public CommandAttribute(Commander commander, CommandOption[] commandOptions) {
        this.commander = commander;
        this.commandOptions = commandOptions;
    }

    public Commander getCommander() {
        return commander;
    }

    public void setCommander(Commander commander) {
        this.commander = commander;
    }

    public CommandOption[] getCommandOptions() {
        return commandOptions;
    }

    public void setCommandOptions(CommandOption[] commandOptions) {
        this.commandOptions = commandOptions;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
