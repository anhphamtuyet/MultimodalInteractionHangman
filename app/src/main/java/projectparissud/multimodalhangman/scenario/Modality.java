package projectparissud.multimodalhangman.scenario;

/**
 * Enumeration to represent the modalities inside the game.
 *
 * @author danilojun
 *
 */
public enum Modality {

    // Input Modalities
    SPEECH_RECOGNITION ("SpeechRecognition"),
    DRAWING ("Drawing"),
    KEYBOARD ("Keyboard"),

    // Output Modalities
    SPEECH_SYNTHESIZER ("SpeechSynthesizer"),
    TEXT ("Text"),
    EARCON ("Earcon"),
    MUSIC ("Music"),
    VIBRATION ("Vibration"),
    PICTURE ("Picture"),

    // Unknown: all other cases
    UNKNOWN ("Unknown");

    // String corresponding to the name of the modality inside the RDF file.
    private String name;

    private Modality(String name) {
        this.name = name;
    }

    /**
     * Converts a String to a Modality.
     *
     * @param s	String to be converted.
     * @return	Modality corresponding to the String.
     */
    public static Modality stringToModality(String s) {
        for (Modality m : Modality.values()) {
            if (m.name.equals(s)) {
                return m;
            }
        }

        return UNKNOWN;
    }

    /**
     * Converts a Modality to a String.
     *
     * @param modality	Modality to be converted.
     * @return			String corresponding to the Modality.
     */
    public static String modalityToString(Modality modality) {
        return modality.name;
    }
}
