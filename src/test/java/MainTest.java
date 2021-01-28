import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MainTest {

    private static Main main;

    @BeforeAll
    static void init() {
        main = new Main();
    }

    @Test
    void arrayAfterFourSimpleGoodArray() {
        Assertions.assertArrayEquals(new Integer[]{5}, main.arrayAfterFour(new Integer[]{1, 2, 3, 4, 5}));
    }

    @Test
    void arrayAfterFourSingleValueOfFourInput() {
        Assertions.assertArrayEquals(new Integer[]{}, main.arrayAfterFour(new Integer[]{4}));
    }

    @Test
    void arrayAfterFourDoubleFourGoodArray() {
        Assertions.assertArrayEquals(new Integer[]{5}, main.arrayAfterFour(new Integer[]{1, 4, 3, 4, 5}));
    }

    @Test
    void arrayAfterFourDoubleFourEndingArray() {
        Assertions.assertArrayEquals(new Integer[]{}, main.arrayAfterFour(new Integer[]{1, 2, 3, 4, 4}));
    }

    @Test
    void arrayAfterFourInvalidArray() {
        Assertions.assertThrows(RuntimeException.class, () -> main.arrayAfterFour(new Integer[]{1, 2, 3}));
    }

    @Test
    void checkArrayHasFourAndOneZero() {
        Assertions.assertEquals(false, main.checkArrayHasFourAndOne(new Integer[]{}));
    }

    @Test
    void checkArrayHasFourAndOneFour() {
        Assertions.assertEquals(false, main.checkArrayHasFourAndOne(new Integer[]{4, 4, 4, 4}));
    }

    @Test
    void checkArrayHasFourAndOneOne() {
        Assertions.assertEquals(false, main.checkArrayHasFourAndOne(new Integer[]{1, 1, 1}));
    }

    @Test
    void checkArrayHasFourAndOneBoth() {
        Assertions.assertEquals(true, main.checkArrayHasFourAndOne(new Integer[]{1, 1, 4, 4, 1}));
    }

    @Test
    void checkArrayHasFourAndOneMultiple() {
        Assertions.assertEquals(true, main.checkArrayHasFourAndOne(new Integer[]{1, 1, 4, 3, 4, 4, 1}));
    }
}