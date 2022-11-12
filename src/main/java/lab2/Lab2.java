package lab2;


import utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;


/**
 * I, Tomáš Hauser, understand that cryptography is easy to mess up, and
 * that I will not carelessly combine pieces of cryptographic ciphers to
 * encrypt my users' data. I will not write crypto code myself, but defer to
 * high-level libaries written by experts who took the right decisions for me,
 * like NaCL.
 */
public class Lab2 {
    public static byte[] encrypt_aes_block(byte[] text, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));

        return cipher.doFinal(text);
    }

    public static byte[] decrypt_aes_block(byte[] hex, byte[] key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));

        byte[] result = cipher.doFinal(hex);

        return result;
    }

    public static byte[] pad(byte[] data) {
        StringBuilder bytesToAdd = new StringBuilder();

        for (int i = 0; i <= 15; i++) {
            if ((data.length + i) % 16 == 0) {
                byte[] ret;
                ret = Arrays.copyOf(data, data.length + i);
                for (int j = data.length; j < data.length + i; j++) {
                    ret[j] = (byte) i;
                }
                return ret;
            }
        }

        return data;
    }

    public static byte[] unpad(byte[] dataWithPadding) {
        byte lastByte = dataWithPadding[dataWithPadding.length - 1];
        return Utils.byteArraySubstring(dataWithPadding, 0, dataWithPadding.length - lastByte);
    }

    public static byte[] encrypt_aes_ecb(byte[] text, byte[] key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] textWithPadding = pad(text);
        byte[] ret = new byte[textWithPadding.length];
        int retIdx = 0;

        for (int i = 0; i < textWithPadding.length; i += 16) {
            byte[] block = Utils.byteArraySubstring(textWithPadding, i, i + 16);
            byte[] encryptedBytes = encrypt_aes_block(block, key);
            for (byte encryptedByte : encryptedBytes) {
                ret[retIdx++] = encryptedByte;
            }
        }

        return ret;
    }

    public static byte[] decrypt_aes_ecb(byte[] text, byte[] key) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] ret = new byte[text.length];

        int retIdx = 0;

        for (int i = 0; i < text.length; i += 16) {
            byte[] block = Utils.byteArraySubstring(text, i, i + 16);
            byte[] decryptedBytes = decrypt_aes_block(block, key);

            for (byte decryptedByte : decryptedBytes) {
                ret[retIdx++] = decryptedByte;
            }
        }

        return text.length % 16 == 0 ? ret : unpad(ret);
    }

    public static String swapFirstAndThirdLine(String s, int charsPerLine) {
        String firstLine = s.substring(0, charsPerLine);
        String secondLine = s.substring(charsPerLine, 2 * charsPerLine);
        String thirdLine = s.substring(2 * charsPerLine, 3 * charsPerLine);
        String restOfTheString = s.substring(3 * charsPerLine);

        return thirdLine + secondLine + firstLine + restOfTheString;
    }

    public static byte[] welcome(String name) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] textToEncrypt = ("Your name is " + name + " and you are a user").getBytes();
        String key = "RIDERSONTHESTORM";
        return encrypt_aes_ecb(textToEncrypt, key.getBytes());
    }

    public static byte[] cipherByUsingWelcome(String blocksSizedWord) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        int neededBlocksToExtract = blocksSizedWord.length() / 16;
        return Utils.getBlockSubstring(welcome("123" + blocksSizedWord + "x".repeat(13)), 2, 2 + neededBlocksToExtract);
    }

    public static byte[] hide_secret(byte[] x) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] SECRET = "this should stay secret".getBytes();
        String key = "COOL T MAGIC KEY";

        return encrypt_aes_ecb(Utils.concatByteArrays(x, SECRET), key.getBytes());
    }

    public static String discover_key() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String foundSecret = "";

        for (int i = 0; i < 15; i++) {
            byte[] as = (("A").repeat(15 - i)).getBytes();
            byte[] cipheredAsFirstBlock = Utils.getBlockSubstring(hide_secret(as), 1, 2);
            byte[] tmp = Arrays.copyOf(as, 16);
            for (int j = 0; j < foundSecret.length(); j++) {
                tmp[tmp.length - 1 - i + j] = (byte) foundSecret.charAt(j);
            }
            for (byte candidateByte = 32; candidateByte < 'z'; candidateByte++) {
                tmp[tmp.length - 1] = candidateByte;
                byte[] cipheredWithCandidate = hide_secret(tmp);
                byte[] cipheredWithCandidateFirstBlock = Utils.getBlockSubstring(cipheredWithCandidate, 1, 2);
                if (Arrays.equals(cipheredAsFirstBlock, cipheredWithCandidateFirstBlock)) {
                    foundSecret += (char) candidateByte;
                    break;
                }
            }
        }

        return foundSecret;
     }

    public static void showcaseLabAnswers() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        // Exercise 2
        String textToEncrypt1 = "90 miles an hour";
        String key1 = "CROSSTOWNTRAFFIC";
        System.out.println("Ciphertext of encrypting '" + textToEncrypt1 + "' with a key '" + key1 + "' is '" + Utils.bytesToPrintableHex(encrypt_aes_block(textToEncrypt1.getBytes(), key1.getBytes())) + "'.");

        // Exercise 3
        String wordToDecrypt = "fad2b9a02d4f9c850f3828751e8d1565";
        String key2 = "VALLEYSOFNEPTUNE";
        System.out.println("Original text of encrypted '" + wordToDecrypt + "' with a key '" + key2 + "' is '" + new String(decrypt_aes_block(HexFormat.of().parseHex(wordToDecrypt), key2.getBytes()), StandardCharsets.US_ASCII) + "'.");

        // Exercise 4
        String wordToAddPaddingTo = "hello";
        String withPadding = new String(pad(wordToAddPaddingTo.getBytes())).replace("\u000B", "0b");
        System.out.println("The word '" + wordToAddPaddingTo + "' with PKCS#7 padding is '" + withPadding + "'.");

        // Exercise 5
        String wordWithPadding = "68656c6c6" + "\u000B".repeat(11);
        String withoutPadding = new String(unpad(wordWithPadding.getBytes()), StandardCharsets.US_ASCII);
        System.out.println("The word '" + wordWithPadding + "' without padding is '" + withoutPadding + "'.");

        // Exercise 6
        String textToEncrypt2 = "Well, I stand up next to a mountain and I chop it down with the edge of my hand";
        String key3 = "vdchldslghtrturn";
        String encryptionResult = Utils.bytesToPrintableHex(encrypt_aes_ecb(textToEncrypt2.getBytes(), key3.getBytes()));
        System.out.println("Ciphertext of encrypting '" + textToEncrypt2 + "' with a key '" + key3 + "' using ecb is '" + encryptionResult + "'.");

        // Exercise 7
        String textToDecrypt = "792c2e2ec4e18e9d3a82f6724cf53848abb28d529a85790923c94b5c5abc34f50929a03550e678949542035cd669d4c66da25e59a5519689b3b4e11a870e7cea";
        String key4 = "If the mountains";
        String decryptionResult = new String(decrypt_aes_ecb(HexFormat.of().parseHex(textToDecrypt), key4.getBytes()), StandardCharsets.US_ASCII);
        System.out.println("The decrypted text is '" + decryptionResult + "'.");

        // Exercise 8
        // 1. There are lots of repeating lines.
        // 2. Padding.
        String textToDecrypt2 = Utils.getResourceTextWithoutNewLines(2, 1);
        // 3. Swapping the first and the third line
        textToDecrypt2 = swapFirstAndThirdLine(textToDecrypt2, 64);
        String key5 = "TLKNGBTMYGNRTION";
        String decryptionResult2 = new String(decrypt_aes_ecb(HexFormat.of().parseHex(textToDecrypt2), key5.getBytes()), StandardCharsets.US_ASCII);
        System.out.println("The first line of the lyrics is '" + decryptionResult2.substring(0, decryptionResult2.indexOf('\n')) + "'.");

        // Exercise 9

        // 2.
        System.out.println("The ciphertext of welcome(\"Jim\") is " + Utils.bytesToPrintableHex(welcome("Jim")) + ".");

        // 3. The string "Your name is " has 13 bytes and " and you are a user" has 19 bytes.
        // Since the 101010... string has 16 bytes then if we add 3 bytes as a prefix and 13 bytes as a suffix,
        // we should see the cipher text as a second block.
        String toGetCiphertextFrom = "\u0010".repeat(16);
        String cipherText = Utils.bytesToPrintableHex(cipherByUsingWelcome(toGetCiphertextFrom));
        System.out.println("The ciphertext of (10)^16 is " + cipherText);
        // 4. The exact same logic
        String toGetCiphertextFrom2 = "you are an admin";
        byte[] cipheredText2 = welcome("123" + toGetCiphertextFrom2 + "x".repeat(13));
        String cipherText2 = Utils.bytesToPrintableHex(cipherByUsingWelcome(toGetCiphertextFrom2));
        System.out.println("The ciphertext of \"you are an admin\" is " + cipherText2);

        // 5. Dam doprostred slovo "CHRISTOPHERSON", tak bude celkova delka 13 + 14 + 21 = 48, coz je delitelne 16,
        // takze zase stejna taktika, jen na konci vezmeme 3 bloky
        String toGetCiphertextFrom3 = "Your name is Christopherson and you are an admin";
        byte[] ciphertext = cipherByUsingWelcome(toGetCiphertextFrom3);
        System.out.println("The ciphertext of \"Your name is Christopherson and you are an admin\" is '" +  Utils.bytesToPrintableHex(ciphertext) + "'.");
        String decryptionResult3 = new String(decrypt_aes_ecb(ciphertext, "RIDERSONTHESTORM".getBytes()), StandardCharsets.US_ASCII);
        System.out.println("Using decrypt_aes_ecb, the plaintext of \"" +  Utils.bytesToPrintableHex(ciphertext) + "\" is '" + decryptionResult3 + "'.");
        //=============================================================================================================

        // Excercise 10
        // The repeating value 1 at the end is the padding
        System.out.println("Discovered key is '" + discover_key() + "'.");
    }
}