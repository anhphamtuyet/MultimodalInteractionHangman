package projectparissud.multimodalhangman.scenario;

/**
 * Class that represents the scenario of the game.
 * A Scenario is composed of a Handicap and an Environment. 
 *
 * @author danilojun
 *
 */
public class Scenario {
    private Handicap handicap;
    private Environment environment;

    /**
     * Constructor.
     *
     * @param handicap		Handicap of the scenario.
     * @param environment	Environment of the scenario.
     */
    public Scenario(Handicap handicap, Environment environment) {
        this.handicap = handicap;
        this.environment = environment;
    }

    /**
     * Gets the handicap of the scenario.
     *
     * @return Handicap.
     */
    public Handicap getHandicap() {
        return handicap;
    }

    /**
     * Gets the environment of the scenario.
     *
     * @return Environment.
     */
    public Environment getEnvironment() {
        return environment;
    }
}
