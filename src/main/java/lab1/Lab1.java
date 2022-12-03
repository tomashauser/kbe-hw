package lab1;

import com.sun.tools.javac.Main;
import utils.Utils;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lab1 {
    private static final Map<Character, Double> relativeFrequenciesMap = Stream.of(new Object[][]{
            {'a', 8.2389258}, {'b', 1.5051398}, {'c', 2.8065007}, {'d', 4.2904556},
            {'e', 12.813865}, {'f', 2.2476217}, {'g', 2.0327458}, {'h', 6.1476691},
            {'i', 6.1476691}, {'j', 0.1543474}, {'k', 0.7787989}, {'l', 4.0604477},
            {'m', 2.4271893}, {'n', 6.8084376}, {'o', 7.5731132}, {'p', 1.9459884},
            {'q', 0.0958366}, {'r', 6.0397268}, {'s', 6.3827211}, {'t', 9.1357551},
            {'u', 2.7822893}, {'v', 0.9866131}, {'w', 2.3807842}, {'x', 0.1513210},
            {'y', 1.9913847}, {'z', 0.0746517},
    }).collect(Collectors.toMap(data -> (Character) data[0], data -> (Double) data[1]));

    private static Double getFittingQuotient(String text) {
        String letters = relativeFrequenciesMap.keySet().stream().map(Object::toString).collect(Collectors.joining());

        double sum = 0;

        for (int i = 0; i < letters.length(); i++) {
            double occurrences = Utils.countOccurrences(text, letters.charAt(i));
            double relativeFrequency = occurrences / text.length();
            double diff = Math.abs(relativeFrequenciesMap.get(letters.charAt(i)) - relativeFrequency);
            sum += diff;
        }

        return sum / relativeFrequenciesMap.size();
    }

    public static String encryptXor(String word, String key) {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < word.length(); i++) {
            ret.append((char) (word.charAt(i) ^ key.charAt(i % key.length())));
        }

        return ret.toString();
    }

    /**
     * The decryption process would be the same as the encryption because  a = b ^ c => b = a ^ c.
     * When only one char is used for the key, every letter is guaranteed to have the same mapping.
     */
    public static String decryptSingleLetterXor(String hexWordEncrypted, char key) {
        return encryptXor(Utils.getTextFromHex(hexWordEncrypted), String.valueOf(key));
    }

    public static DecryptionAnswer decryptSingleLetterXor(String hexWordEncrypted) {
        String allPossibleKeys = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        String text = Utils.getTextFromHex(hexWordEncrypted);

        double bestFittingQuotient = Double.MAX_VALUE;
        String decryptionWithTheBestFittingQuotient = null;
        String key = null;

        for (int i = 0; i < allPossibleKeys.length(); i++) {
            String possibleDecryption;

            possibleDecryption = encryptXor(text, String.valueOf(allPossibleKeys.charAt(i)));

            double fittingQuotient = getFittingQuotient(possibleDecryption);

            if (fittingQuotient < bestFittingQuotient) {
                bestFittingQuotient = fittingQuotient;
                decryptionWithTheBestFittingQuotient = possibleDecryption;
                key = String.valueOf(allPossibleKeys.charAt(i));
            }
        }

        return new DecryptionAnswer(key, decryptionWithTheBestFittingQuotient);
    }

    public static DecryptionAnswer decryptXorForGivenKeyLength(String hexWordEncrypted, int length) {
        String text = Utils.getTextFromHex(hexWordEncrypted);
        StringBuilder key = new StringBuilder();

        for (int k = 0; k < length; k++) {
            String everyKthCharacter = Utils.getEveryKthCharacterFromString(text, k, length);
            key.append(decryptSingleLetterXor(Utils.getHexFromText(everyKthCharacter)).getKey());
        }

        return new DecryptionAnswer(key.toString(), encryptXor(text, key.toString()));
    }

    public static DecryptionAnswer decryptXor(String hexWordEncrypted, int searchUpperBound) {
        double bestFittingQuotient = Double.MAX_VALUE;
        DecryptionAnswer bestDecryptionAnswer = null;

        for (int i = 1; i <= searchUpperBound; i++) {
            DecryptionAnswer decryptionAnswer = decryptXorForGivenKeyLength(hexWordEncrypted, i);

            double fittingQuotient = getFittingQuotient(decryptionAnswer.getDecryptedMessage());

            if (fittingQuotient < bestFittingQuotient) {
                bestFittingQuotient = fittingQuotient;
                bestDecryptionAnswer = decryptionAnswer;
            }
        }

        return bestDecryptionAnswer;
    }

    public static void showcaseLabAnswers() throws IOException {
        // Exercise 1
        String wordToEncrypt = "the world is yours";
        String xorKey = "illmatic";
        String encryptedWord = Utils.getHexFromText(encryptXor(wordToEncrypt, xorKey));
        System.out.println("1. The xor encryption of '" + wordToEncrypt + "' with the key '" + xorKey + "' is '" + encryptedWord + "'.");

        // Exercise 2
        String encryptedMessage = "404b48484504404b48484504464d4848045d4b";
        char singleLetterKey = '$';
        String decryptedMessage = decryptSingleLetterXor(encryptedMessage, singleLetterKey);
        System.out.println("2. The first line of decrypted '" + encryptedMessage + "' with a key '" + singleLetterKey + "' is '" + Utils.getFirstLine(decryptedMessage + "'."));

        // Exercise 3, 4
        String text1 = Utils.getResourceTextWithoutNewLines(1,1);
        String decryptedText1Demo = decryptSingleLetterXor(text1).getDecryptedMessage().substring(0, 100).replace("\n", " ");
        System.out.println("3. and 4. The first line of decrypted text from text1.hex based on a fitting quotient minimizing  is '" + Utils.getFirstLine(decryptedText1Demo) + "'.");

        // Exercise 5
        String text2 = Utils.getResourceTextWithoutNewLines(1, 2);
        String decryptedText2Demo = decryptXorForGivenKeyLength(text2, 10).getDecryptedMessage();
        System.out.println("5. The first line of decrypted text from text2.hex is '" + Utils.getFirstLine(decryptedText2Demo) + "'.");

        // Exercise 6
        String text3 = Utils.getResourceTextWithoutNewLines(1, 3);
        String decryptedText3Demo = decryptXor(text3, 20).getDecryptedMessage();
        System.out.println("6. The first line of decrypted text from text3.hex is '" + Utils.getFirstLine(decryptedText3Demo) + "'.");
    }
}
