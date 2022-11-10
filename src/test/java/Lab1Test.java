import lab1.Lab1;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class Lab1Test {
    @Test
    public void encryptXor() {
        String word = "everything remains raw";
        String key = "word up";
        String expected = "121917165901181e01154452101d16061c1700071100";
        String actual = Lab1.encryptXor(word, key);

        assertEquals(expected, actual);
    }
}
