package com.cloudcore.minder.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileSystem {
	 /* Fields */
    public static final String RootPath = Paths.get("").toAbsolutePath().toString() + File.separator;

    public static String CommandFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_COMMAND + File.separator;
    public static String LogsFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_LOGS + File.separator
            + Config.TAG_BACKUPER + File.separator;
    public static String AccountFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_ACCOUNTS 
            + File.separator + Config.TAG_PASSWORDS + File.separator;
    public static String backupFolder = LogsFolder + Config.TAG_BACKUP_DEFAULT + File.separator;

    public static String GalleryFolder = RootPath + Config.TAG_GALLERY + File.separator;
    public static String BankFolder = RootPath + Config.TAG_BANK + File.separator;
    public static String FrackedFolder = RootPath + Config.TAG_FRACKED + File.separator;

    public static String Tag_account = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_ACCOUNTS;
//    public static String TemplateFolder = RootPath + Config.TAG_TEMPLATES + File.separator;


    /* Methods */
    /**
     * Get the current time for creating backup folder
     *
     * @return current time in specified format
     */
    public static String getBackUpTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MMM dd HH.mm ss a");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Creates directories in the location defined by RootPath.
     *
     * @return true if all folders were created or already exist, otherwise
     * false.
     */
    public static boolean createDirectories() {
        try {

            Files.createDirectories(Paths.get(RootPath));
            Files.createDirectories(Paths.get(CommandFolder));
            Files.createDirectories(Paths.get(LogsFolder));
            Files.createDirectories(Paths.get(AccountFolder));
            Files.createDirectories(Paths.get(backupFolder));
//            Files.createDirectories(Paths.get(GalleryFolder));
//            Files.createDirectories(Paths.get(BankFolder));
//            Files.createDirectories(Paths.get(FrackedFolder));
        } catch (IOException e) {
            System.out.println("FS#CD: " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    /**
     * Creates directories in the location defined by RootPath.
     *
     * @param path for the folder to backup CloudCoin files.
     *
     * @return true if backup folders were created, otherwise false.
     */
    public static boolean createBackupDirectory(String path) {
        try {

            if (!path.isEmpty()) {
                Path createDirectories = Files.createDirectories(Paths.get(path + File.separator + Config.TAG_BACKUP + getBackUpTime()));
                backupFolder = createDirectories.toString();
            } else {
                Path createDirectories = Files.createDirectories(Paths.get(backupFolder + Config.TAG_BACKUP + getBackUpTime()));
                backupFolder = createDirectories.toString();
            }
        } catch (IOException e) {
            System.out.println("FS#CD: " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    /**
     * Creates directories in the location defined by RootPath.
     *
     * @param acccount_pass to create all folders for backup.
     *
     * @return true if backup folders were created, otherwise false.
     */
    public static boolean createFoldersWithAccountPassword(String acccount_pass) {
        try {

            if (!acccount_pass.isEmpty()) {
                Path createDirectories = Files.createDirectories(Paths.get(Tag_account + File.separator + acccount_pass.trim() + File.separator));
                GalleryFolder = Files.createDirectories(Paths.get(createDirectories.toString() + File.separator + Config.TAG_GALLERY + File.separator)).toString();
                BankFolder = Files.createDirectories(Paths.get(createDirectories.toString() + File.separator + Config.TAG_BANK + File.separator)).toString();
                FrackedFolder = Files.createDirectories(Paths.get(createDirectories.toString() + File.separator + Config.TAG_FRACKED + File.separator)).toString();
            } else {
                Path createDirectories = Files.createDirectories(Paths.get(Tag_account + File.separator + getBackUpTime() + File.separator));
                GalleryFolder = Files.createDirectories(Paths.get(createDirectories.toString() + File.separator + Config.TAG_GALLERY + File.separator)).toString();
                BankFolder = Files.createDirectories(Paths.get(createDirectories.toString() + File.separator + Config.TAG_BANK + File.separator)).toString();
                FrackedFolder = Files.createDirectories(Paths.get(createDirectories.toString() + File.separator + Config.TAG_FRACKED + File.separator)).toString();
           
            }
        } catch (IOException e) {
            System.out.println("FS#CD: " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    /**
     * Creates directories in the location defined by RootPath.
     *
     * @param sourceDirectory directory which will be backup.
     * @param destinationDirectory in which the backup will be taken
     *
     *
     * @return true if backup is taken successfully.
     */
    public static boolean copyFiles(File sourceDirectory, File destinationDirectory) {

        try {
            if (sourceDirectory.isDirectory()) {
                if (!destinationDirectory.exists()) {
                    destinationDirectory.mkdir();
                }

                String[] children = sourceDirectory.list();
                for (String children1 : children) {
                    copyFiles(new File(sourceDirectory, children1), new File(destinationDirectory, children1));
                }
            } else {

                OutputStream out;
                try (InputStream in = new FileInputStream(sourceDirectory)) {
                    out = new FileOutputStream(destinationDirectory);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                out.close();
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
