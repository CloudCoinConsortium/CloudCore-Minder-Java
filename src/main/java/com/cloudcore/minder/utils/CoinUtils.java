package com.cloudcore.minder.utils;

import com.cloudcore.minder.core.CloudCoin;

public class CoinUtils {
	/* Methods */

    private static int[] denominations = new int[] {1, 5, 25, 100, 250};
    /**
     * Returns a denomination matching the index value of the denomination.
     *
     * @param index index of denomination
     * @return 1, 5, 25, 100, 250, or -1 if the index is invalid.
     */
    public static int getDenomination(int index) {
        if (index < 0 || index >= denominations.length)
            return -1;

        return denominations[index];
    }

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
}
