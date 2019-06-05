package nl.avans.wordcrex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JUnit5ExampleTest {

    @Test
    @DisplayName("Test if stuff is equal")
    void isEqual() {
        var expected = 2 * 2;
        var actual = 4 * 1;

        assertEquals(expected, actual);
    }
}