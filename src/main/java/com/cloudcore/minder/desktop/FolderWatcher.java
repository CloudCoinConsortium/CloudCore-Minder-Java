package com.cloudcore.minder.desktop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class FolderWatcher {
	// Fields

    /** A WatchService to detect folder changes. */
    private WatchService watcher;

    /** The directory to be watched by the WatchService. */
    private String directory;


    // Constructors

    /** Constructor for objects of class FolderWatcher. */
    public FolderWatcher(String directory) {
        this.directory = directory;
        Initialize(directory);
    }


    // Methods

    /**
     * Initializes a new WatchService to detect new files at the specified directory.
     *
     * @param directory the directory to be watched.
     */
    private void Initialize(String directory) {
        try {
            File folder = new File(directory);
            if (!folder.exists())
                folder.mkdir();
            Path path = Paths.get(directory);
            watcher = path.getFileSystem().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
        }
    }

    /**
     * Detects if a new file was created in the specified directory.
     *
     * @return {@code true} if a new file was created, otherwise returns {@code false}.
     */
    public boolean newFileDetected() {
        WatchKey watchKey = watcher.poll();
        if (watchKey == null)
            return false;

        List<WatchEvent<?>> events = watchKey.pollEvents();
        for (int i = 0; i < events.size(); i++) {
            WatchEvent event = events.get(i);
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                resetWatcher(watchKey);
                return true;
            }
        }

        resetWatcher(watchKey);
        return false;
    }

    /**
     * Resets the WatchKey to re-queue the Watch Service. If the WatchKey does not successfully reset, then create a
     * new WatchService.
     *
     * @param watchKey The WatchKey generated
     */
    private void resetWatcher(WatchKey watchKey) {
        if (!watchKey.reset()) {
            try {
                watcher.close();
            } catch (IOException e) {
            } finally {
                watcher = null;
            }

            Initialize(directory);
        }
    }
}
