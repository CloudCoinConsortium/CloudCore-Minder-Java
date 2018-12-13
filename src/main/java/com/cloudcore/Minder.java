package com.cloudcore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.cloudcore.authenticator.raida.Node;
import com.cloudcore.authenticator.raida.RAIDA;
import com.cloudcore.minder.core.CloudCoin;
import com.cloudcore.minder.core.FileSystem;
import com.cloudcore.minder.utils.SimpleLogger;

public class Minder {

	public static int NetworkNumber = 1;

	public static boolean toMinder(String password, String cloudCoinAmount) {
        RAIDA raida = GetNetwork();
        
        if (raida == null)
            return false;

        ArrayList<CloudCoin> coins = getCoins(cloudCoinAmount, true);
		if (coins == null)
			return false;
		
		ArrayList<CompletableFuture<Node.MultiDetectResponse>> tasks = raida.getMultiDetectTasks(raida.coins, true, password);
        try {
            try {
                System.out.println("Waiting for futures...");
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
            } catch (Exception e) {
                System.out.println("RAIDA#PNC:" + e.getLocalizedMessage());
            }

            for (int j = 0; j < coins.size(); j++) {
                CloudCoin coin = coins.get(j);
                coin.setAn((ArrayList<String>) Arrays.asList(coin.pan));
    			coin.setFolder(FileSystem.MinderFolder);
    			FileSystem.moveAndUpdateCoin(coin, FileSystem.BankFolder, FileSystem.MinderFolder);
    			String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss:SSS")).toLowerCase();
    			SimpleLogger.writeLog(currentDate + " [toMinder] " + coin.getSn() + "\n");
            }
        } catch (Exception e) {
            System.out.println("RAIDA#PNC: " + e.getLocalizedMessage());
        }

		return true;
	}

	public static boolean fromMinder(String password, String cloudCoinAmount) {
		RAIDA raida = GetNetwork();
		
		if (raida == null)
			return false;
		
		ArrayList<CloudCoin> coins = getCoins(cloudCoinAmount, false);
		if (coins == null)
			return false;
		
		ArrayList<CompletableFuture<Node.MultiDetectResponse>> tasks = raida.getMultiDetectTasks(raida.coins, false, password);
        try {
            try {
                System.out.println("Waiting for futures...");
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
            } catch (Exception e) {
                System.out.println("RAIDA#PNC:" + e.getLocalizedMessage());
            }

            for (int j = 0; j < coins.size(); j++) {
                CloudCoin coin = coins.get(j);
                coin.setAn((ArrayList<String>) Arrays.asList(coin.pan));
    			coin.setFolder(FileSystem.BankFolder);
    			FileSystem.moveAndUpdateCoin(coin, FileSystem.MinderFolder, FileSystem.BankFolder);
    			String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss:SSS")).toLowerCase();
    			SimpleLogger.writeLog(currentDate + " [FromMinder] " + coin.getSn() + "\n");
            }
        } catch (Exception e) {
            System.out.println("RAIDA#PNC: " + e.getLocalizedMessage());
        }

		return true;
	}
	
	private static RAIDA GetNetwork() {
		System.out.println("Getting network...");
        RAIDA raida = null;
        for (RAIDA network : RAIDA.networks) {
            if (network != null && NetworkNumber == network.networkNumber) {
                raida = network;
                break;
            }
        }

        if (raida == null)
            return null;
        
        return raida;
	}
	
	private static ArrayList<CloudCoin> getCoins(String cloudCoinAmount, boolean isMinding) {
		String folderName;
		if (isMinding) {
			System.out.println(Instant.now().toString() + ": Minding coins......");
			folderName = FileSystem.BankFolder;
		} else {
			System.out.println(Instant.now().toString() + ": Moving coins to Bank......");
			folderName = FileSystem.MinderFolder;
		}
		
		ArrayList<CloudCoin> coins;
		if (cloudCoinAmount.equals("all"))
			coins = FileSystem.loadFolderCoins(folderName);
		else {
			try {
				coins = FileSystem.loadCoinsAmount(Integer.parseInt(cloudCoinAmount), folderName);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return coins;
	}
}
