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
            singleRun = isSingleRun(args);
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
                    exitIfSingleRun();
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
                        exitIfSingleRun();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Uncaught exception - " + e.getLocalizedMessage());
        }
    }

    public static boolean singleRun = false;
    public static boolean isSingleRun(String[] args) {
        for (String arg : args)
            if (arg.equals("singleRun"))
                return true;
        return false;
    }
    public static void exitIfSingleRun() {
        if (singleRun)
            System.exit(0);
    }
}
