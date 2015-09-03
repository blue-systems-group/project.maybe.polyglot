public class Hello {
    // TODO: no code for initialize variables
    private int i = maybe("i") {1, 2, 3};
    private Hello self = maybe("self") {new Hello()};

    public static String getLabel(String label) {
        return "" + label;
    }

    public static void maybeVariable() {
        // DONE: can indentify below.
        // DONE: implement this.
        // int b = maybe("b") {10, 2, 3}, c = maybe("c") {3, 2, 1}, d = maybe("d") {4, 5, 6}, e = 2;
        // // TODO: pass init checker
        // System.out.println(b);

        // DONE: implement mechanism
        // DONE: fix bug, maybe expression can't be visitChild
        int a = 1;
        String label = "a";
        // TODO: fix edu.buffalo.cse.blue.maybe.ast.MaybeLocalAssign_c cannot be cast to polyglot.ast.Assign
        // a = maybe(label) {1, 2, 3};
        // a = maybe(getLabel(label)) {1, 2, 3};
        // a = maybe(label + label) {1, 2, 3};
        // a = maybe(label + "a") {1, 2, 3};
        // System.out.println(a);

        // a = maybe("a") {1, 2, 3};
        // TODO: fix edu.buffalo.cse.blue.maybe.ast.MaybeLocalAssign_c cannot be cast to polyglot.ast.Assign
        // a *= maybe("a") {1, 2, 3};
        // a /= maybe("a") {1, 2, 3};
        // a %= maybe("a") {1, 2, 3};
        // a += maybe("a") {1, 2, 3};
        // a -= maybe("a") {1, 2, 3};
        //
        // a <<= maybe("a") {1, 2, 3};
        // a >>= maybe("a") {1, 2, 3};
        // a >>>= maybe("a") {1, 2, 3};
        //
        // a &= maybe("a") {1, 2, 3};
        // a ^= maybe("a") {1, 2, 3};
        // a |= maybe("a") {1, 2, 3};

        int[] array = new int[3];
        array = new int[]{1, 2, 3};
        // TODO: syntax error
        // array = maybe("array") {{1}, {2}, {3}};
        // TODO: error below
        // array[0] = maybe("array") {1, 2, 3};
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
