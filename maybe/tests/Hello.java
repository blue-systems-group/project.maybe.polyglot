public class Hello {
    // TODO: no code for initialize variables
    // TODO: need to discuss whether support this style
    private int i;
    private Hello self;

    public static String getLabel(String label) {
        return "" + label;
    }

    public static void maybeVariable() {
        // DONE: can indentify below.
        // DONE: implement this.
        // int b = maybe("b") {10, 2, 3}, c = maybe("c") {3, 2, 1}, d = maybe("d") {4, 5, 6}, e = 2;
        // TODO: edu.buffalo.cse.blue.maybe.ast.MaybeLocalDecl_c cannot be cast to polyglot.ast.Expr
        int b = maybe ("b") {1, 2, 3}, bb = maybe("bb") {1, 2};
        // // TODO: pass init checker
        // System.out.println(b);

        // DONE: implement mechanism
        // DONE: fix bug, maybe expression can't be visitChild
        int a = 1;
        String label = "a";
        // DONE: fix edu.buffalo.cse.blue.maybe.ast.MaybeLocalAssign_c cannot be cast to polyglot.ast.Assign
        a = maybe("a") {1, 2, 3};
        // DONE: syntax error prompt
        // a = maybe(label) {};
        System.out.println(a);
        String str = "";
        str = maybe("str") {"1", "2"};

        a = maybe("c") {1, 2, 3};
        // DONE: fix edu.buffalo.cse.blue.maybe.ast.MaybeLocalAssign_c cannot be cast to polyglot.ast.Assign
        // DONE: The += operator must have numeric operands.
        // DONE: use real typeCheck in MaybeAssignExt
        a *= maybe("d") {1, 2, 3};
        a /= maybe("e") {1, 2, 3};
        a %= maybe("f") {1, 2, 3};
        a += maybe("g") {1, 2, 3};
        a -= maybe("h") {1, 2, 3};

        a <<= maybe("i") {1, 2, 3};
        a >>= maybe("j") {1, 2, 3};
        a >>>= maybe("k") {1, 2, 3};

        a &= maybe("l") {1, 2, 3};
        a ^= maybe("m") {1, 2, 3};
        a |= maybe("n") {1, 2, 3};

        int[] array = new int[3];
        array = new int[]{1, 2, 3};
        // TODO: syntax error
        // array = maybe("array") {{1}, {2}, {3}};
        // TODO: error below
        array[0] = maybe("array") {1, 2, 3};

        int c;
        c = maybe("o") {1, 2, 3};
        a = c;
    }

    public static void main(String[] args) {
        maybeVariable();

        String label = "one alternative ";
        maybe ("abcd") {
            System.out.println(label);
        }
        // DONE: duplicate label check
        // maybe ("abcd") {
        //     System.out.println(label);
        // }

        label = "Two alternatives ";
        maybe ("2") {
            System.out.println(label + "0");
        } or {
            System.out.println(label + "1");
        }

        label = "Multiple alternatives ";
        maybe ("3") {
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
        maybe ("4") {
            maybe ("5") {
                System.out.println(label + level + l + " 0");
            } or {
                System.out.println(label + level + l + " 1");
            }
        } or {
            l++;
            maybe ("6") {
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
