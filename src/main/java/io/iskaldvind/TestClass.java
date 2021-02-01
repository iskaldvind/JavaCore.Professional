package io.iskaldvind;

import io.iskaldvind.annotations.BeforeSuite;
import io.iskaldvind.annotations.AfterSuite;
import io.iskaldvind.annotations.Test;

public class TestClass {

    @BeforeSuite
    public static void setUp() {
        System.out.println("Before tests");
    }

    @Test(priority = 1)
    public static void testPriority1() {
        System.out.println("Priority 1 test");
    }

    @Test(priority = 2)
    public static void testPriority2() {
        System.out.println("Priority 2 test");
    }

    @Test(priority = 3)
    public static void testPriority3() {
        System.out.println("Priority 3 test");
    }

    @Test(priority = 4)
    public static void testPriority4() {
        System.out.println("Priority 4 test");
    }

    @Test(priority = 5)
    public static void testPriority5() {
        System.out.println("Priority 5 test");
    }

    @Test(priority = 6)
    public static void testPriority6() {
        System.out.println("Priority 6 test");
    }

    @Test(priority = 7)
    public static void testPriority7() {
        System.out.println("Priority 7 test");
    }

    @Test(priority = 8)
    public static void testPriority8() {
        System.out.println("Priority 8 test");
    }

    @Test(priority = 9)
    public static void testPriority9() {
        System.out.println("Priority 9 test");
    }

    @Test(priority = 10)
    public static void testPriority10() {
        System.out.println("Priority 10 test");
    }

    @AfterSuite
    public static void tearDown() {
        System.out.println("After tests");
    }
}
