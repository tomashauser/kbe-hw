package lab3;

import utils.CipherTextAndIv;
import utils.Utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lab2.Lab2.*;

public class Lab3 {
    public static byte[] xor(byte[] b1, byte[] b2) throws Exception {
        if (b1.length != b2.length) throw new Exception("b1 and b2 must have the same length");

        byte[] res = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            res[i] = (byte) (b1[i] ^ b2[i]);
        }

        return res;
    }

    public static byte[] encrypt_aes_cbc(byte[] message, byte[] keyHex, byte[] iv) throws Exception {
        byte[] paddedMessage = pad(message);
        byte[] ret = new byte[paddedMessage.length];
        int retIdx = 0;

        for (int i = 0; i < paddedMessage.length; i += 16) {
            byte[] block = Utils.byteArraySubstring(paddedMessage, i, i + 16);
            byte[] xorredBlock = xor(block, iv);

            byte[] encryptedBytes = encrypt_aes_block(xorredBlock, keyHex);
            iv = encryptedBytes;

            for (byte encryptedByte : encryptedBytes) {
                ret[retIdx++] = encryptedByte;
            }
        }

        return ret;
    }

    public static byte[] decrypt_aes_cbc(byte[] encryptedMessage, byte[] key, byte[] iv) throws Exception {
        return decrypt_aes_cbc(encryptedMessage, key, iv, false);
    }

    public static byte[] decrypt_aes_cbc(byte[] encryptedMessage, byte[] key, byte[] iv, boolean forceUnpad) throws Exception {
        byte[] ret = new byte[encryptedMessage.length];
        int retIdx = 0;

        byte[] originalIv = iv;

        for (int i = 0; i < encryptedMessage.length; i += 16) {
            byte[] block = Utils.byteArraySubstring(encryptedMessage, i, i + 16);

            byte[] decryptedBytes = decrypt_aes_block(block, key);

            byte[] xorredBlock = xor(decryptedBytes, iv);

            iv = block;

            for (byte encryptedByte : xorredBlock) {
                ret[retIdx++] = encryptedByte;
            }
        }

        byte[] unpaddedRet = unpad(ret, forceUnpad);

        return unpaddedRet;
    }

    public static byte[] generate_iv() {
        SecureRandom csprng = new SecureRandom();
        byte[] randomBytes = new byte[16];
        csprng.nextBytes(randomBytes);
        return randomBytes;
    }

    public static CipherTextAndIv server_encrypt() throws Exception {
        byte[] iv = generate_iv(); //TODO: Generate
        //byte[] iv = {29, 127, 8, 118, 120, -42, 40, -71, 21, 123, 51, 50, -8, 96, 127, -40};
       // byte[] iv = Utils.getByteArrayFromString(Utils.getTextFromHex("a1b27b4eeef364f9da74a8c06edbd771"));
        String plaintext = "this data is top secret, can you decrypt it?";
        String key = "nzbighzuxgjsoajg";
        byte[] cipherText = encrypt_aes_cbc(plaintext.getBytes(), key.getBytes(), iv);
        return new CipherTextAndIv(cipherText, iv);
    }

    public static boolean server_decrypt(byte[] cipherText, byte[] iv) {
        String key = "nzbighzuxgjsoajg";
        try {
            byte[] plaintext = decrypt_aes_cbc(cipherText, key.getBytes(), iv, true);
         //   System.out.println("Decrypted text: " + new String(plaintext));
            return true;
        } catch (Exception e) {
          //  System.out.println("SERVER_DECRYPT: " + e.getMessage());
            return false;
        }
    }

    public static byte[] decrypt_server(CipherTextAndIv hiddenCipherTextAndIv) throws Exception {
        byte[] originalIv = hiddenCipherTextAndIv.iv;
        byte[] curIv = Arrays.copyOf(originalIv, originalIv.length);

        StringBuilder cipherText = new StringBuilder();

        for (int b = 0; b < hiddenCipherTextAndIv.cipherText.length / 16; b++) {
            byte[] hiddenCipherCurBlock = Utils.getBlockSubstring(hiddenCipherTextAndIv.cipherText, b + 1, b + 2);
            StringBuilder curCipherText = new StringBuilder();
            List<Integer> lastIdxs = new ArrayList<>();
            for (int i = 0; i < hiddenCipherCurBlock.length; i++) {
                for (int j = 0; j < i; j++) {
                    curIv[curIv.length - j - 1] = (byte) ((i + 1) ^ ((j + 1) ^ lastIdxs.get(j)));
                }
                for (int j = -128; j <= 127; j++) {
                    curIv[curIv.length - i - 1] = (byte) j;
                    if (server_decrypt(hiddenCipherCurBlock, curIv)) {
                        byte decipheredByte = (byte) (((i + 1) ^ j) ^ originalIv[originalIv.length - i - 1]);
                        char decipheredLetter = (char) decipheredByte;
                        curCipherText.append(decipheredLetter);
                        lastIdxs.add(j);
                        break;
                    }
                }
            }
            cipherText.append(curCipherText.reverse());
            originalIv = hiddenCipherCurBlock;
            curIv = Arrays.copyOf(originalIv, originalIv.length);
        }

        return unpad(cipherText.toString().getBytes());
    }

    public static void showcaseLabAnswers() throws Exception {
        // Exercise 1
        String message = "we are always running for the thrill of it";
        String key = "WALKINGONADREAM.";
        String ivHex = "a1b27b4eeef364f9da74a8c06edbd771";
        byte[] ivBytes = Utils.getByteArrayFromString(Utils.getTextFromHex(ivHex));
        byte[] res = encrypt_aes_cbc(message.getBytes(), key.getBytes(), ivBytes);
        String printableRes = Utils.bytesToPrintableHex(res);
        System.out.println("1. The cipher text of '" + message + "' with a key '" + key + "' and an IV '" + ivHex + "' is '" + printableRes + "'.");

        // Exercise 2
        String decryptedRes = new String(decrypt_aes_cbc(res, key.getBytes(), ivBytes));
        System.out.println("2. The plaintext of '" + printableRes + "' is '" + decryptedRes + "'.");

        // Exercise 3
        String welcomeToThisCar = "welcome to this car";
        String welcomeToThisCarKey = "nckdlgyzsklvheba";
        String ivHex2 = "fbd71a63197605dde3ac8bce86c1ead7";
        byte[] welcomeToThisCarIv = Utils.getByteArrayFromString(Utils.getTextFromHex(ivHex2));
        byte[] welcomeToThisCarBytes = welcomeToThisCar.getBytes();
        byte[] welcomeToThisCarCipherText = encrypt_aes_cbc(welcomeToThisCar.getBytes(), welcomeToThisCarKey.getBytes(), welcomeToThisCarIv);
        String printableCipherText = Utils.bytesToPrintableHex(welcomeToThisCarCipherText);
        byte[] welcomeToThisBarCipherText =  Arrays.copyOf(welcomeToThisCarCipherText, welcomeToThisCarCipherText.length);
        byte[] welcomeToThisWarCipherText = Arrays.copyOf(welcomeToThisCarCipherText, welcomeToThisCarCipherText.length);
        welcomeToThisBarCipherText[0] = (byte) (welcomeToThisCarCipherText[0] ^ 99 ^ 98);
        String plaintextOfWelcomeToThisBar = new String(decrypt_aes_cbc(welcomeToThisBarCipherText, welcomeToThisCarKey.getBytes(), welcomeToThisCarIv));
        System.out.println("3. a) The first byte of the second block of '" + welcomeToThisCar + "' is " + welcomeToThisCarBytes[16] + ", aka " + Utils.getBinFromText(welcomeToThisCar.substring(16, 17)) + ".");
        System.out.println("      The letter 'b' has an ASCII representation '" + Utils.getBinFromText("b") + "'; therefore we would need to make the last bit of 'c' zero in order to obtain 'b'.");
        System.out.println("   b) The ciphertext of '" + welcomeToThisCar + "' with a key '" + welcomeToThisCarKey + "' and an IV '" + ivHex2 + "' is '" + printableCipherText + "'.");
        System.out.println("   c) We can achieve a 'b' in the position of 'c' by assigning: welcomeToThisBarCipherText[0] = welcomeToThisCarCipherText[0] ^ 'c' ^ 'b'");
        System.out.println("      Deciphering an edited ciphertext '" + Utils.bytesToPrintableHex(welcomeToThisBarCipherText) + "' yields '" + plaintextOfWelcomeToThisBar + "'.");
        System.out.println("   d) We can achieve a 'w' in the position of 'c' by assigning: welcomeToThisWarCipherText[0] = welcomeToThisCarCipherText[0] ^ 'c' ^ 'w'");
        welcomeToThisWarCipherText[0] = (byte) (welcomeToThisCarCipherText[0] ^ 99 ^ 119);
        String plaintextOfWelcomeToThisWar = new String(decrypt_aes_cbc(welcomeToThisWarCipherText, welcomeToThisCarKey.getBytes(), welcomeToThisCarIv));
        System.out.println("      Deciphering an edited ciphertext '" + Utils.bytesToPrintableHex(welcomeToThisWarCipherText) + "' yields '" + plaintextOfWelcomeToThisWar + "'.");
        String welcomeToThisBar = "welcome to this bar";
        // Exercise 4
        System.out.println("4. CSPRNG stands for " + "Cryptographically Secure Pseudorandom Number Generator.");
        System.out.println("   The difference between CSPRNG and PRNG is that although CSPRNG satisfies all the requirements that PRNG does, the reverse is not true - it has to pass several other tests such as the 'next-bit test' or 'statce compromise extensions' in order to pass ass cryptographically secure.");

        // Exercise 6
        boolean ok = false;
        while(!ok) {
            try {
                CipherTextAndIv hiddenCipherTextAndIv = server_encrypt();
                String serverDecryption = new String(decrypt_server(hiddenCipherTextAndIv));
                System.out.println("5. The server decryption result for a randomly generated iv '" + Utils.bytesToPrintableHex(hiddenCipherTextAndIv.iv) + "' is '" + serverDecryption + "'.");
                ok = true;
            } catch (Exception e) {

            }
        }
    }
}
