public class Hello {
    public static String getLabel(String label) {
        return "" + label;
    }

    public static void maybeVariable() {
        // TODO: implement mechanism
        int a = 1;
        String label = "a";
        a = maybe(label) {1, 2, 3};
        a = maybe(getlabel(label)) {1, 2, 3};
        a = maybe(label + label) {1, 2, 3};
        a = maybe(label + "a") {1, 2, 3};

        a = maybe("a") {1, 2, 3};
        a *= maybe("a") {1, 2, 3};
        a /= maybe("a") {1, 2, 3};
        a %= maybe("a") {1, 2, 3};
        a += maybe("a") {1, 2, 3};
        a -= maybe("a") {1, 2, 3};

        a <<= maybe("a") {1, 2, 3};
        a >>= maybe("a") {1, 2, 3};
        a >>>= maybe("a") {1, 2, 3};

        a &= maybe("a") {1, 2, 3};
        a ^= maybe("a") {1, 2, 3};
        a |= maybe("a") {1, 2, 3};

        int[] array = new int[3];
        array = new int[]{1, 2, 3};
        // TODO: syntax error
        // array = maybe("array") {{1}, {2}, {3}};
        // TODO: error below
        // array[0] = maybe("array") {1, 2, 3};

        // DONE: can indentify below.
        // TODO: implement this.
        // int b = maybe("b") {10, 2, 3};
    }

    public static void main(String[] args) {
        maybeVariable();

        String label = "one alternative ";
        maybe (label) {
            System.out.println(label);
        }

        label = "Two alternatives ";
        maybe (label) {
            System.out.println(label + "0");
        } or {
            System.out.println(label + "1");
        }

        label = "Multiple alternatives ";
        maybe (label) {
            System.out.println(label + "0");
        } or {
            System.out.println(label + "1");
        } or {
            System.out.println(label + "2");
        } or {
            System.out.println(label + "3");
        } or {
            System.out.println(label + "4");
        }

        label = "Nested maybe statements ";
        String level = "levels ";
        int l = 0;
        maybe (label) {
            maybe (level) {
                System.out.println(label + level + l + " 0");
            } or {
                System.out.println(label + level + l + " 1");
            }
        } or {
            l++;
            maybe (level) {
                System.out.println(label + level + l + " 0");
            } or {
                System.out.println(label + level + l + " 1");
            }
        }
        // if (false) {
        //     b++;
        // } else {
        //     c++;
        // }

        // maybe ("3") {
        //     int a = 0;
        // } or {
        //     int b = 1;
        // } or {
        //     int c = 2;
        // }

        System.out.println("Hello world!");
    }
}
