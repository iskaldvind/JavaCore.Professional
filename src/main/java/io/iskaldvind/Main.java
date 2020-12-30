package io.iskaldvind;

import io.iskaldvind.containers.Box;
import io.iskaldvind.fruits.Apple;
import io.iskaldvind.fruits.Orange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        String[] stringArray = {"1st", "2nd", "3rd"};
        Integer[] integerArray = {1, 2, 3};

        System.out.println(Arrays.toString(stringArray) + " switch 1 and 3 elements: " + Arrays.toString(switchElements(stringArray, 0, 2)));
        System.out.println(Arrays.toString(integerArray) + " switch 3 and 1 elements: " + Arrays.toString(switchElements(integerArray, 2, 0)));

        System.out.println(Arrays.toString(integerArray) + " as ArrayList: " + arrayToArrayList(integerArray));

        Box<Apple> applesBox1 = new Box<>();
        applesBox1.add(new Apple());
        applesBox1.add(new Apple());
        applesBox1.add(new Apple());

        System.out.println("ApplesBox №1 weight is " + applesBox1.getWeight());

        Box<Apple> applesBox2 = new Box<>();
        applesBox2.add(new Apple());

        Box<Orange> orangesBox1 = new Box<>();
        orangesBox1.add(new Orange());
        orangesBox1.add(new Orange());

        System.out.println("ApplesBox №1 and ApplesBox №2 have " + ( applesBox1.compare(applesBox2) ? "" : "not " ) + "equal weights");
        System.out.println("ApplesBox №1 and OrangesBox №1 have " + ( applesBox1.compare(orangesBox1) ? "" : "not " ) + "equal weights");

        applesBox1.fillFrom(applesBox2);

        System.out.println("Now ApplesBox №1 weight is " + applesBox1.getWeight());
    }

    private static <T> T[] switchElements(T[] array, int index1, int index2) {
        if (index1 < 0 || index1 >= array.length) {
            System.out.println("First element index is out of bounds");
        } else {
            if (index2 < 0 || index2 >= array.length) {
                System.out.println("First element index is out of bounds");
            } else {
                T buffer = array[index1];
                array[index1] = array[index2];
                array[index2] = buffer;
                return array;
            }
        }
        return null;
    }
    
    private static <T> ArrayList<T> arrayToArrayList(T[] array) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }
}
