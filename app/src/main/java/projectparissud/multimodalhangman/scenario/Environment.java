package projectparissud.multimodalhangman.scenario;
/**
 * Enumeration to represent the environments inside the game.
 *
 * @author danilojun
 *
 */
public enum Environment {
    QUIET ("Quiet"),
    NOISY ("Noisy"),
    SKI ("Ski"),
    UNKNOWN ("Unknown");

    // String corresponding to the name of the environment inside the RDF file.
    private String name;

    private Environment(String name) {
        this.name = name;
    }

    /**
     * Converts a String to an Environment.
     *
     * @param s	String to be converted.
     * @return	Environment corresponding to the String.
     */
    public static Environment stringToEnvironment(String s) {
        for (Environment e : Environment.values()) {
            if (e.name.equals(s)) {
                return e;
            }
        }

        return UNKNOWN;
    }

    /**
     * Converts an Environment into a String.
     *
     * @param environment	Environment to be converted.
     * @return				String corresponding to the Environment.
     */
    public static String environmentToString(Environment environment) {
        return environment.name;
    }
}
