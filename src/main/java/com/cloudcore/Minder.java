package com.cloudcore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;

import com.cloudcore.minder.core.CloudCoin;
import com.cloudcore.minder.core.FileSystem;
import com.cloudcore.minder.utils.Utils;

public class Minder {
	
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
        byte[] hashBytes = encrypter.digest(password.getBytes(StandardCharsets.UTF_8));

        for (CloudCoin coin : coins) {
            ArrayList<String> vans = new ArrayList<>();

            for (String an : coin.getAn()) {
                // Get decimals from AN
                long octet = Long.parseLong(an.substring(8, 16), 16);
                long decimal1 = Long.parseLong(Long.toString(octet, 10));
                octet = Long.parseLong(an.substring(16, 24), 16);
                long decimal2 = Long.parseLong(Long.toString(octet, 10));

                // Get decimals from MD5 Hash
                String hash = Utils.toHexadecimal(hashBytes);
                octet = Long.parseLong(hash.substring(0, 8), 16);
                long decimal3 = Long.parseLong(Long.toString(octet, 10));
                octet = Long.parseLong(hash.substring(8, 16), 16);
                long decimal4 = Long.parseLong(Long.toString(octet, 10));

                // Subtract numbers and convert to Hex
                long van1 = decimal1 - decimal3;
                long van2 = decimal2 - decimal4;
                String hex1 = Utils.padString(Long.toHexString(Math.abs(van1)), 8, '0');
                String hex2 = Utils.padString(Long.toHexString(Math.abs(van2)), 8, '0');

                System.out.println(hex1 + ", " + hex2);

                // Update new AN
                StringBuilder builder = new StringBuilder();
                builder.append(an, 0, 8);
                if (van1 < 0) {
                    builder.append('(');
                    builder.append(hex1);
                    builder.append(')');
                }
                else
                    builder.append(hex1);
                if (van2 < 0) {
                    builder.append('(');
                    builder.append(hex2);
                    builder.append(')');
                }
                else
                    builder.append(hex2);
                builder.append(an, 24, 32);
                vans.add(builder.toString().toLowerCase());
            }

            coin.setAn(vans);
            //coin.setFolder(FileSystem.VaultFolder);
            //FileSystem.moveAndUpdateCoin(coin, FileSystem.BankFolder, FileSystem.VaultFolder);
        }
		return false;
	}
	
	public static boolean fromMinder(String password, String cloudCoinAmount) {
		return true;
	}
}
