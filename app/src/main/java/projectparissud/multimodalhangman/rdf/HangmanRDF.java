package projectparissud.multimodalhangman.rdf;

import java.util.ArrayList;

import projectparissud.multimodalhangman.scenario.Modality;
import projectparissud.multimodalhangman.scenario.Scenario;

/**
 * Interface between the game and the RDF files.
 * It is used to get the available input/output modalities of the game,
 * based on the current scenario.
 *
 * @author danilojun
 *
 */
public interface HangmanRDF {
	
	/*
	 * Important Remark: RDF Repository object != RDF file
	 * 
	 * The RFD Repository object is an object created in the Sesame API to 
	 * represent the information of the RDF file inside the program, while the
	 * RDF file is the .rdf file that we read.
	 * Changing the RDF Repository object does not change the RDF file, it only
	 * changes the repository inside the program. So, to persists modifications 
	 * in the RDF Repository object, we have to write over the RDF file (and 
	 * that is why we have the saveCurrentScenario method).
	 * 
	 */

    /**
     * Sets the current scenario of the RDF Repository object.
     *
     * @param	scenario Scenario to be set
     */
    public void setCurrentScenario(Scenario scenario);

    /**
     * Returns a Scenario object corresponding to the current scenario of the
     * RDF Repository object.
     *
     * @return	Current scenario
     */
    public Scenario getCurrentScenario();

    /**
     * Resets the current scenario of the RDF Repository object, based on the
     * scenario present in the RDF file.
     * It sets the current scenario of the Repository object as the scenario
     * present in the RDF file.
     */
    public void resetCurrentScenario();

    /**
     *
     * Warning ! This method does not work with Android, because it is not possible to write
     * into a file in the assets folder :(
     *
     * Saves the current scenario into the RDF file.
     * It writes the current scenario of the RDF Repository object into the
     * RDF file, while the method setCurrentScenario simply changes the
     * current scenario only inside the RDF repository object of the Sesame API.
     * This method is used to persist the current scenario into the RDF file,
     * so that the user can have his scenario saved for the next session of the
     * game.
     */
    public void saveCurrentScenario();

    /**
     * Returns an ArrayList of the available input modalities, based on the
     * current scenario, that is, the handicap and the environment.
     *
     * @return ArrayList of the available input modalities
     */
    public ArrayList<Modality> getAvailableInputModalities();

    /**
     * Returns an ArrayList of the available output modalities, based on the
     * current scenario, that is, the handicap and the environment.
     *
     * @return ArrayList of the available output modalities
     */
    public ArrayList<Modality> getAvailableOutputModalities();

}
