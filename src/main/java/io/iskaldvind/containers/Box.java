package io.iskaldvind.containers;

import io.iskaldvind.fruits.Fruit;

import java.util.ArrayList;

public class Box<T extends Fruit> {

    private static final int CAPACITY = 100;
    private final ArrayList<T> STORAGE = new ArrayList<>();
    private float weight = 0f;

    public float getWeight() {
        return this.weight;
    }

    public void add(T fruit) {
        if (this.STORAGE.size() < CAPACITY) {
            this.STORAGE.add(fruit);
            this.weight += fruit.getWeight();
        } else {
            System.out.println("This box is full");
        }
    }

    private void removeTop() {
        int fruits = this.STORAGE.size();
        if (fruits > 0) {
            T fruit = this.STORAGE.get(fruits - 1);
            this.STORAGE.remove(fruits - 1);
            this.weight = Math.min(this.weight - fruit.getWeight(), 0);
        } else {
            System.out.println("This box is empty");
        }
    }

    public boolean compare(Box<?> compareWith) {
        return Math.abs(this.getWeight() - compareWith.getWeight()) < 0.001;
    }

    public void fillFrom(Box<T> sourceBox) {
        int places = CAPACITY - this.STORAGE.size();
        int fruits = Math.min(places, sourceBox.STORAGE.size());
        for (int i = 0; i < fruits; i++) {
            int sourceBoxElementIndex = sourceBox.STORAGE.size() - 1;
            T fruit = sourceBox.STORAGE.get(sourceBoxElementIndex);
            add(fruit);
            sourceBox.removeTop();
        }
    }
}
