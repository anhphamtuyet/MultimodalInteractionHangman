package projectparissud.multimodalhangman.scenario;

/**
 * Enumeration to represent the handicaps inside the game.
 *
 * @author danilojun
 *
 */
public enum Handicap {
    NONE ("None"),
    BLIND ("Blind"),
    DEAF ("Deaf"),
    ARMLESS ("Armless"),
    UNKNOWN ("Unknown");

    // String corresponding to the name of the handicap inside the RDF file.
    private String name;

    private Handicap(String name) {
        this.name = name;
    }

    /**
     * Converts a String to a Handicap.
     *
     * @param s	String to be converted.
     * @return	Handicap corresponding to the String.
     */
    public static Handicap stringToHandicap(String s) {
        for (Handicap h : Handicap.values()) {
            if (h.name.equals(s)) {
                return h;
            }
        }

        return UNKNOWN;
    }

    /**
     * Converts a Handicap to a String.
     *
     * @param handicap	Handicap to be converted.
     * @return			String corresponding to the Handicap.
     */
    public static String handicapToString(Handicap handicap) {
        return handicap.name;
    }
}
