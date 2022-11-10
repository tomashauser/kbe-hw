package lab1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Utils {
    private static String normalizeBinChar(String str) {
        int dif = 8 - str.length();

        String missingZeros = "00000000".substring(0, dif);

        return new StringBuilder(str).insert(0, missingZeros).toString();
    }

    private static String normalizeHexChar(String str) {
        return str.length() == 1 ? ("0" + str) : str;
    }

    public static String getBinFromText(String str) {
        return str.chars().mapToObj(c -> normalizeBinChar(Integer.toBinaryString(c))).collect(Collectors.joining());
    }

    public static String getHexFromText(String str) {
        return str.chars().mapToObj(c -> normalizeHexChar(Integer.toHexString(c))).collect(Collectors.joining());
    }

    public static String getTextFromBin(String bin) {
        return Arrays.stream(bin.split("(?<=\\G.{8})"))
                .map(s -> String.valueOf((char) Integer.parseInt(s, 2)))
                .collect(Collectors.joining());
    }

    public static String getTextFromHex(String hex) {
        return Arrays.stream(hex.split("(?<=\\G.{2})"))
                .map(s -> String.valueOf((char) Integer.parseInt(s, 16)))
                .collect(Collectors.joining());
    }

    public static String getHexFromBin(String bin) {
        return getHexFromText(getTextFromBin(bin));
    }

    public static String getBinFromHex(String hex) {
        return getBinFromText(getTextFromHex(hex));
    }

    public static int countOccurrences(String text, char toCount) {
        int count = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == toCount) {
                count++;
            }
        }

        return count;
    }

    public static String getEveryKthCharacterFromString(String str, int k, int length) {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i + k < str.length(); i += length) {
            ret.append(str.charAt(i + k));
        }

        return ret.toString();
    }

    public static String getResourceTextWithoutNewLines(int number) throws IOException {
        String fileName = "/text" + number + ".hex";
        InputStream is = Lab1.class.getResourceAsStream(fileName);

        assert is != null;

        return new String(is.readAllBytes()).replace("\n", "");
    }

    public static String getFirstLine(String str) {
        int indexOfNewLine = str.indexOf('\n');
        return indexOfNewLine < 0 ? str : str.substring(0, str.indexOf('\n'));
    }
}
