package ua.algorithms.lab1.util;

import java.util.Objects;

public class MutableInt implements Comparable<MutableInt> {
    private Integer value;

    private MutableInt() {
        value = 0;
    }

    private MutableInt(Integer value) {
        this.value = value;
    }

    public static MutableInt defaultValue() {
        return new MutableInt();
    }

    public static MutableInt of(Integer value) {
        return new MutableInt(value);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void increment() {
        value++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableInt that = (MutableInt) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(MutableInt o) {
        return Integer.compare(this.getValue().compareTo(o.getValue()), 0);
    }
}
