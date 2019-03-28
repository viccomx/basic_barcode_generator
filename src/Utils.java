public class Utils {

    private static boolean verbosity = false;

    public static void setVerbosity(boolean enable) {
        verbosity = enable;
    }

    public static boolean getVerbosity(){
        return verbosity;
    }

    public static void info(String info) {
        if (verbosity) {
            System.out.println(info);
        }
    }
}
