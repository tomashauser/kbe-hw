import lab1.Lab1;
import lab2.Lab2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    private static void printNthLabHeader(int n) {
        System.out.println("=".repeat(10) + "[lab1.Lab " + n + "]" + "=".repeat(10));
    }

    private static void printFooter() {
        System.out.println("=".repeat(20 + 7));
    }

    public static void main(String[] args) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Lab2.showcaseLabAnswers();
    }
}