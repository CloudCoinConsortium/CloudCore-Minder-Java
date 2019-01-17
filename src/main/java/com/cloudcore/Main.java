package com.cloudcore;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.cloudcore.minder.core.Command;
import com.cloudcore.minder.core.FileSystem;
import com.cloudcore.minder.desktop.FolderWatcher;
import com.cloudcore.minder.utils.SimpleLogger;

public class Main {
	
    public static void main(String[] args) {
        ArrayList<Command> commands;
        
        try {
            if (args.length != 0 && Files.exists(Paths.get(args[0]))) {
                System.out.println("New root path: " + args[0]);
                FileSystem.changeRootPath(args[0]);
            }
            FileSystem.createDirectories();

            FolderWatcher watcher = new FolderWatcher(FileSystem.CommandFolder);
            boolean stop = false;

            commands = FileSystem.getCommands();
            if (commands.size() > 0)
                for (Command command : commands) {
                    boolean successful;
                    if ("toMind".equals(command.command))
                        successful = Minder.toMinder(command.passphrase, command.cloudCoinAmount);
                    else if ("fromMind".equals(command.command))
                        successful = Minder.fromMinder(command.passphrase, command.cloudCoinAmount);
                }

            while (!stop) {
                if (watcher.newFileDetected()) {
                    commands = FileSystem.getCommands();
                    for (Command command : commands) {
                        boolean successful;
                        if ("toMind".equals(command.command))
                            successful = Minder.toMinder(command.passphrase, command.cloudCoinAmount);
                        else if ("fromMind".equals(command.command))
                            successful = Minder.fromMinder(command.passphrase, command.cloudCoinAmount);
                        FileSystem.archiveCommand(command);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Uncaught exception - " + e.getLocalizedMessage());
        }
    }
}
