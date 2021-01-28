import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

    }

    public Integer[] arrayAfterFour(Integer[] inputArray) throws RuntimeException {
        if (!checkArrayHasFour(inputArray)) throw new RuntimeException("Array must contain 4");
        ArrayList<Integer> newArray = new ArrayList<>();
        for (int i = inputArray.length - 1; i >= 0; i--) {
            if (inputArray[i] == 4) break;
            newArray.add(inputArray[i]);
        }
        Integer[] result = new Integer[newArray.size()];
        return newArray.toArray(result);
    }

    private boolean checkArrayHasFour(Integer[] inputArray) {
        for (int el: inputArray) {
            if (el == 4) return true;
        }
        return false;
    }

    public boolean checkArrayHasFourAndOne(Integer[] inputArray) {
        int foundInt = 0;
        for (int el: inputArray) {
            if (el == 4) {
                if (foundInt == 1) {
                    return true;
                } else {
                    foundInt = 4;
                }
            } else if (el == 1) {
                if (foundInt == 4) {
                    return true;
                } else {
                    foundInt = 1;
                }
            } else {
                return false;
            }
        }
        return false;
    }
}
