package com.learning.command;

import com.learning.command.dto.CommandOption;

public interface Commander {
    void execute(CommandOption[] commandOptions, Object obj);
}
