
public class Foo {
    public void sumNumbers(int x, int y) {
        int z = x + y;
    }
    public void sumNumbers() {
        int x = 5;
        int y = x + x;
        int z = y + x;
    }

    public void subNumbers() {
        int x = 5;
        int y = x - x;
        int z = y - x;
    }

    public void multNumbers(int x, int y) {
        int z = y * x;
    }
}
public class Bar {
    public void numbers() {
        int x = 4;
        int y = x + x;
        int z = y * x;
    }
}