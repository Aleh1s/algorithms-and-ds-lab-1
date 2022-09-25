package ua.algorithms.lab1.util;

import java.util.Objects;

public class MutableLong implements Comparable<MutableLong> {
    private Long value;

    private MutableLong() {
        value = 0L;
    }

    private MutableLong(Long value) {
        this.value = value;
    }

    public static MutableLong defaultValue() {
        return new MutableLong();
    }

    public static MutableLong of(Long value) {
        return new MutableLong(value);
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public void increment() {
        value++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableLong that = (MutableLong) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(MutableLong o) {
        return Long.compare(this.getValue().compareTo(o.getValue()), 0L);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
