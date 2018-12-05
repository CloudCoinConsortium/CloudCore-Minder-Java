package com.cloudcore.minder.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.cloudcore.minder.utils.CoinUtils;
import com.cloudcore.minder.utils.FileUtils;
import com.cloudcore.minder.utils.SimpleLogger;
import com.cloudcore.minder.utils.Utils;

import com.google.gson.Gson;

public class FileSystem {
	 /* Fields */
    public static final String RootPath = Paths.get("").toAbsolutePath().toString() + File.separator;

    public static String CommandFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_COMMAND + File.separator;
    public static String LogsFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_LOGS + File.separator
            + Config.TAG_MINDER + File.separator;
    public static String AccountFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_ACCOUNTS 
            + File.separator + Config.TAG_PASSWORDS + File.separator;

    public static String GalleryFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_GALLERY + File.separator;
    public static String BankFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator +  Config.TAG_BANK + File.separator;
    public static String MinderFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_MINDER+ File.separator;
    public static String FrackedFolder = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_FRACKED + File.separator;

    public static String Tag_account = RootPath + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_CLOUD_COIN + File.separator + Config.TAG_ACCOUNTS;
//    public static String TemplateFolder = RootPath + Config.TAG_TEMPLATES + File.separator;


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
            Files.createDirectories(Paths.get(BankFolder));
            Files.createDirectories(Paths.get(MinderFolder));
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
    
    public static int[] getTotalCoins() {
        return getTotalCoins(BankFolder);
    }
    public static int[] getTotalCoins(String accountFolder) {
        int[] totals = new int[6];

        int[] bankTotals = FileUtils.countCoins(accountFolder);

        totals[5] = bankTotals[0];
        totals[0] = bankTotals[1];
        totals[1] = bankTotals[2];
        totals[2] = bankTotals[3];
        totals[3] = bankTotals[4];
        totals[4] = bankTotals[5];

        return totals;
    }

    /**
     * Loads all CloudCoins from a specific folder.
     *
     * @param folder the folder to search for CloudCoin files.
     * @return an ArrayList of all CloudCoins in the specified folder.
     */
    public static ArrayList<CloudCoin> loadFolderCoins(String folder) {
        ArrayList<CloudCoin> folderCoins = new ArrayList<>();

        String[] filenames = FileUtils.selectFileNamesInFolder(folder);
        for (String filename : filenames) {
            int index = filename.lastIndexOf('.');
            if (index == -1) continue;

            String extension = filename.substring(index + 1);

            switch (extension) {
                case "stack":
                    ArrayList<CloudCoin> coins = FileUtils.loadCloudCoinsFromStack(folder, filename);
                    folderCoins.addAll(coins);
                    break;
            }
        }

        return folderCoins;
    }

    public static ArrayList<Command> getCommands() {
        String[] commandFiles = FileUtils.selectFileNamesInFolder(CommandFolder);
        ArrayList<Command> commands = new ArrayList<>();

        for (int i = 0, j = commandFiles.length; i < j; i++) {
            if (!commandFiles[i].contains(Config.TAG_file_name))
                continue;

            try {
                String json = new String(Files.readAllBytes(Paths.get(CommandFolder + commandFiles[i])));
                Command command = Utils.createGson().fromJson(json, Command.class);
                command.filename = commandFiles[i];
                commands.add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return commands;
    }

    public static void archiveCommand(Command command) {
        try {
            Files.move(Paths.get(CommandFolder + command.filename),
                    Paths.get(LogsFolder + command.filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void moveCoins(ArrayList<CloudCoin> coins, String sourceFolder, String targetFolder) {
        moveCoins(coins, sourceFolder, targetFolder, ".stack");
    }
    public static void moveCoins(ArrayList<CloudCoin> coins, String sourceFolder, String targetFolder, String extension) {
        for (CloudCoin coin : coins) {
            String fileName = FileUtils.ensureFilenameUnique(CoinUtils.generateFilename(coin), extension, targetFolder);

            try {
                Files.move(Paths.get(sourceFolder + coin.currentFilename), Paths.get(targetFolder + fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                String fileSeparator = (File.separatorChar == '\\') ? "\\\\" : File.separator;
                String[] folders = targetFolder.split(fileSeparator);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void moveAndUpdateCoin(CloudCoin coin, String sourceFolder, String targetFolder) {
        moveAndUpdateCoin(coin, sourceFolder, targetFolder, ".stack");
    }
    public static void moveAndUpdateCoin(CloudCoin coin, String sourceFolder, String targetFolder, String extension) {
        Gson gson = Utils.createGson();
        String fileName = FileUtils.ensureFilenameUnique(CoinUtils.generateFilename(coin), extension, targetFolder);
        try {
            Stack stack = new Stack(coin);
            Files.write(Paths.get(targetFolder + fileName), gson.toJson(stack).getBytes(), StandardOpenOption.CREATE_NEW);
            Files.deleteIfExists(Paths.get(sourceFolder + coin.currentFilename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<CloudCoin> loadCoinsAmount(int amount, String folder) {
        int[] bankTotals = FileUtils.countCoins(folder);
        int[] totals = new int[6];
        totals[0] = bankTotals[0];
        totals[1] = bankTotals[1];
        totals[2] = bankTotals[2];
        totals[3] = bankTotals[3];
        totals[4] = bankTotals[4];
        totals[5] = bankTotals[5];

        if (amount > totals[0] || amount <= 0)
            return null;

        int exp_1 = 0;
        int exp_5 = 0;
        int exp_25 = 0;
        int exp_100 = 0;
        int exp_250 = 0;
        if (amount >= 250 && totals[5] > 0) {
            exp_250 = ((amount / 250) < (totals[5])) ? (amount / 250) : (totals[5]);
            amount -= (exp_250 * 250);
        }
        if (amount >= 100 && totals[4] > 0) {
            exp_100 = ((amount / 100) < (totals[4])) ? (amount / 100) : (totals[4]);
            amount -= (exp_100 * 100);
        }
        if (amount >= 25 && totals[3] > 0) {
            exp_25 = ((amount / 25) < (totals[3])) ? (amount / 25) : (totals[3]);
            amount -= (exp_25 * 25);
        }
        if (amount >= 5 && totals[2] > 0) {
            exp_5 = ((amount / 5) < (totals[2])) ? (amount / 5) : (totals[2]);
            amount -= (exp_5 * 5);
        }
        if (amount >= 1 && totals[1] > 0) {
            exp_1 = (amount < (totals[1])) ? amount : (totals[1]);
            amount -= (exp_1);
        }

        if (amount != 0) {
            return null;
        }

        // Get the CloudCoins that will be used for the export.
        int totalSaved = exp_1 + (exp_5 * 5) + (exp_25 * 25) + (exp_100 * 100) + (exp_250 * 250);
        ArrayList<CloudCoin> totalCoins = FileSystem.loadFolderCoins(folder);

        ArrayList<CloudCoin> onesToExport = new ArrayList<>();
        ArrayList<CloudCoin> fivesToExport = new ArrayList<>();
        ArrayList<CloudCoin> qtrToExport = new ArrayList<>();
        ArrayList<CloudCoin> hundredsToExport = new ArrayList<>();
        ArrayList<CloudCoin> twoFiftiesToExport = new ArrayList<>();

        for (int i = 0, totalCoinsSize = totalCoins.size(); i < totalCoinsSize; i++) {
            CloudCoin coin = totalCoins.get(i);
            int denomination = CoinUtils.getDenomination(coin);
            if (denomination == 1) {
                if (exp_1-- > 0) onesToExport.add(coin);
                else exp_1 = 0;
            } else if (denomination == 5) {
                if (exp_5-- > 0) fivesToExport.add(coin);
                else exp_5 = 0;
            } else if (denomination == 25) {
                if (exp_25-- > 0) qtrToExport.add(coin);
                else exp_25 = 0;
            } else if (denomination == 100) {
                if (exp_100-- > 0) hundredsToExport.add(coin);
                else exp_100 = 0;
            } else if (denomination == 250) {
                if (exp_250-- > 0) twoFiftiesToExport.add(coin);
                else exp_250 = 0;
            }
        }

        if (onesToExport.size() < exp_1 || fivesToExport.size() < exp_5 || qtrToExport.size() < exp_25
                || hundredsToExport.size() < exp_100 || twoFiftiesToExport.size() < exp_250) {
            return null;
        }

        ArrayList<CloudCoin> exportCoins = new ArrayList<>();
        exportCoins.addAll(onesToExport);
        exportCoins.addAll(fivesToExport);
        exportCoins.addAll(qtrToExport);
        exportCoins.addAll(hundredsToExport);
        exportCoins.addAll(twoFiftiesToExport);

        return exportCoins;
    }
}
