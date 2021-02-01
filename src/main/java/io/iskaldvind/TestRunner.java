package io.iskaldvind;

import io.iskaldvind.annotations.AfterSuite;
import io.iskaldvind.annotations.BeforeSuite;
import io.iskaldvind.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class TestRunner {

    public static void start(Class<?> testClass) {
        Method[] allDeclaredMethods = testClass.getDeclaredMethods();
        Method[] beforeMethods = Arrays.stream(allDeclaredMethods).filter(it -> it.isAnnotationPresent(BeforeSuite.class)).toArray(Method[]::new);
        Method[] afterMethods = Arrays.stream(allDeclaredMethods).filter(it -> it.isAnnotationPresent(AfterSuite.class)).toArray(Method[]::new);
        if (beforeMethods.length != 1 || afterMethods.length != 1) {
            throw new RuntimeException("Wrong number of Suite annotaions");
        }
        invoke(beforeMethods[0]);
        Arrays.stream(allDeclaredMethods)
                .filter(it -> it.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparingInt(it -> it.getAnnotation(Test.class).priority()))
                .forEach(TestRunner::invoke);
        invoke(afterMethods[0]);
    }

    private static void invoke(Method method) {
        try {
            method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
