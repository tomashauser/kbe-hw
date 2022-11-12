import lab1.Lab1;
import lab2.Lab2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    static int reps = 50;

    private static void printNthLabHeader(int n) {

        System.out.println("=".repeat(reps) + "[Lab " + n + "]" + "=".repeat(reps));
    }

    private static void printFooter() {
        System.out.println("=".repeat(reps + 7));
    }

    public static void main(String[] args) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        printNthLabHeader(1);
        Lab1.showcaseLabAnswers();
        printNthLabHeader(2);
        Lab2.showcaseLabAnswers();
        printFooter();
    }
}