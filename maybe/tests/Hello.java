public class Hello {
    public static String getLabel(String label) {
        return "maybe_" + label;
    }
    public static void main(String[] args) {
        // int a = maybe ("0") 1, 2, 3;
        maybe("1") {
            System.out.println("only one block");
        }

        int a = 0, b = 1, c = 3;
        String label = "1234";
        maybe (getLabel(label)) {
            b++;
        } or {
            c++;
        } or {
            b--;
        } or {
            c--;
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
