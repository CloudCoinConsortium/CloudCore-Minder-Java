package com.cloudcore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.cloudcore.minder.core.CloudCoin;
import com.cloudcore.minder.core.Config;
import com.cloudcore.minder.core.FileSystem;
import com.cloudcore.minder.utils.SimpleLogger;

public class Minder {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static boolean toMinder(String password, String cloudCoinAmount) {
		System.out.println(Instant.now().toString() + ": Minding coins......");
		ArrayList<CloudCoin> coins;
		if (cloudCoinAmount.equals("all"))
			coins = FileSystem.loadFolderCoins(FileSystem.BankFolder);
		else {
			try {
				coins = FileSystem.loadCoinsAmount(Integer.parseInt(cloudCoinAmount), FileSystem.BankFolder);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		if (coins == null)
			return false;

		MessageDigest encrypter;
		try {
			encrypter = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}

		for (CloudCoin coin : coins) {
			ArrayList<String> vans = new ArrayList<>();

			for (int i = 0; i < Config.nodeCount; i++) {
				String hashSeed = coin.getSn() + i + password.toLowerCase();
				byte[] hashBytes = encrypter.digest(hashSeed.getBytes(StandardCharsets.UTF_8));
				vans.add(bytesToHex(hashBytes).toLowerCase());
			}

			coin.setAn(vans);
			coin.setFolder(FileSystem.MinderFolder);
			FileSystem.moveAndUpdateCoin(coin, FileSystem.BankFolder, FileSystem.MinderFolder);
			String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss:SSS")).toLowerCase();
			SimpleLogger.writeLog(currentDate + " [toMinder] " + coin.getSn() + "\n");
		}
		return true;
	}

	public static boolean fromMinder(String password, String cloudCoinAmount) {
		return true;
	}
}
