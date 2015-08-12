public class Hello {
    public static void main(String[] args) {
        // int a = maybe ("1") 1, 2, 3;

        maybe("123" == "1") {
            int b = 0;
        } or {
            int b = 1;
        }

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
