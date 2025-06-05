package br.edu.fs.simulator.util;

public class MyStringBuilder {
    private char[] value;
    private int count;
    private static final int DEFAULT_CAPACITY = 16;

    public MyStringBuilder() {
        this.value = new char[DEFAULT_CAPACITY];
        this.count = 0;
    }

    public MyStringBuilder(int capacity) {
        this.value = new char[capacity];
        this.count = 0;
    }

    public MyStringBuilder(String str) {
        this.count = str.length();
        this.value = new char[this.count + DEFAULT_CAPACITY];
        str.getChars(0, this.count, this.value, 0);
    }

    private void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > value.length) {
            expandCapacity(minimumCapacity);
        }
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (value.length * 2) + 2;
        if (newCapacity < minimumCapacity) {
            newCapacity = minimumCapacity;
        }
        if (newCapacity < 0) { // overflow
            if (minimumCapacity < 0) throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
        char[] newValue = new char[newCapacity];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
    }

    public int length() {
        return count;
    }

    public MyStringBuilder append(String str) {
        if (str == null) str = "null";
        int len = str.length();
        ensureCapacity(count + len);
        str.getChars(0, len, value, count);
        count += len;
        return this;
    }

    public MyStringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }

    public MyStringBuilder append(char c) {
        ensureCapacity(count + 1);
        value[count++] = c;
        return this;
    }

    public MyStringBuilder append(char[] str) {
        int len = str.length;
        ensureCapacity(count + len);
        System.arraycopy(str, 0, value, count, len);
        count += len;
        return this;
    }

    public MyStringBuilder append(boolean b) {
        return append(String.valueOf(b));
    }

    public MyStringBuilder append(int i) {
        return append(String.valueOf(i));
    }

    public MyStringBuilder append(long l) {
        return append(String.valueOf(l));
    }

    public MyStringBuilder append(float f) {
        return append(String.valueOf(f));
    }

    public MyStringBuilder append(double d) {
        return append(String.valueOf(d));
    }

    public String toString() {
        return new String(value, 0, count);
    }

    public char charAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    public MyStringBuilder deleteCharAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        System.arraycopy(value, index + 1, value, index, count - index - 1);
        count--;
        return this;
    }

    public MyStringBuilder delete(int start, int end) {
        if (start < 0) throw new StringIndexOutOfBoundsException(start);
        if (end > count) end = count;
        if (start > end) throw new StringIndexOutOfBoundsException();

        int len = end - start;
        if (len > 0) {
            System.arraycopy(value, start + len, value, start, count - end);
            count -= len;
        }
        return this;
    }

    public MyStringBuilder insert(int offset, String str) {
        if ((offset < 0) || (offset > count)) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (str == null) str = "null";
        int len = str.length();
        ensureCapacity(count + len);
        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count += len;
        return this;
    }

    public void clear() {
        count = 0;
    }
}