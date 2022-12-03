import lab1.Lab1;
import lab2.Lab2;
import lab3.Lab3;

public class Main {
    static int reps = 50;

    private static void printNthLabHeader(int n) {

        System.out.println("=".repeat(reps) + "[Lab " + n + "]" + "=".repeat(reps));
    }

    private static void printFooter() {
        System.out.println("=".repeat(2 * reps + 7));
    }

    public static void main(String[] args) throws Exception {
        printNthLabHeader(1);
        Lab1.showcaseLabAnswers();
        printNthLabHeader(2);
        Lab2.showcaseLabAnswers();
        printNthLabHeader(3);
        Lab3.showcaseLabAnswers();
        printFooter();
    }
}