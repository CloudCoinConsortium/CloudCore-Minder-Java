package com.cloudcore.authenticator.utils;

import com.cloudcore.authenticator.core.CloudCoin;
import com.cloudcore.authenticator.core.Config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

public class CoinUtils {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    /* Methods */

    /**
     * Returns a denomination describing the currency value of the CloudCoin.
     *
     * @param coin CloudCoin
     * @return 1, 5, 25, 100, 250, or 0 if the CloudCoin's serial number is invalid.
     */
    public static int getDenomination(CloudCoin coin) {
        int sn = coin.getSn();
        int nom;
        if (sn < 1)
            nom = 0;
        else if ((sn < 2097153))
            nom = 1;
        else if ((sn < 4194305))
            nom = 5;
        else if ((sn < 6291457))
            nom = 25;
        else if ((sn < 14680065))
            nom = 100;
        else if ((sn < 16777217))
            nom = 250;
        else
            nom = 0;

        return nom;
    }

    /**
     * Generates a name for the CloudCoin based on the denomination, Network Number, and Serial Number.
     * <br>
     * <br>Example: 25.1.6123456
     *
     * @return String a filename
     */
    public static String generateFilename(CloudCoin coin) {
        return getDenomination(coin) + ".CloudCoin." + coin.getNn() + "." + coin.getSn();
    }

    /**
     * Generates secure random GUIDs for pans. An example:
     * <ul>
     * <li>8d3eb063937164c789474f2a82c146d3</li>
     * </ul>
     * These Strings are hexadecimal and have a length of 32.
     */
    public static void generatePAN(CloudCoin coin) {
        coin.pan = new String[Config.nodeCount];
        for (int i = 0; i < Config.nodeCount; i++) {
            SecureRandom random = new SecureRandom();
            byte[] cryptoRandomBuffer = random.generateSeed(16);

            UUID uuid = UUID.nameUUIDFromBytes(cryptoRandomBuffer);
            coin.pan[i] = uuid.toString().replace("-", "");
        }
    }
    
    /**
     * Generates pans with hash
     * @param coin
     */
    public static void generatePANWithHash(CloudCoin coin, String password) {
		MessageDigest encrypter;
		try {
			encrypter = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		
		ArrayList<String> vans = new ArrayList<>();
		for (int i = 0; i < Config.nodeCount; i++) {
			String hashSeed = coin.getSn() + i + password.toLowerCase();
			byte[] hashBytes = encrypter.digest(hashSeed.getBytes(StandardCharsets.UTF_8));
			coin.pan[i] = bytesToHex(hashBytes).toLowerCase();
		}

    }
    
    public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

    public static int getPassCount(CloudCoin coin) {
        return Utils.charCount(coin.getPown(), 'p');
    }
    public static int getFailCount(CloudCoin coin) {
        return Utils.charCount(coin.getPown(), 'f');
    }
    public static String getDetectionResult(CloudCoin coin) {
        return (getPassCount(coin) >= Config.passCount) ? "Pass" : "Fail";
    }

    /**
     * Updates the Authenticity Numbers to the new Proposed Authenticity Numbers.
     */
    public static void setAnsToPans(CloudCoin coin) {
        for (int i = 0; (i < Config.nodeCount); i++) {
            coin.getAn().set(i, coin.pan[i]);
        }
    }
}
