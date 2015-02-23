package projectparissud.multimodalhangman.rdf;

import android.content.res.AssetManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;

import projectparissud.multimodalhangman.scenario.Environment;
import projectparissud.multimodalhangman.scenario.Handicap;
import projectparissud.multimodalhangman.scenario.Modality;
import projectparissud.multimodalhangman.scenario.Scenario;

/**
 * Implementation of the HangmanRDF interface.
 *
 * @author danilojun
 *
 */
public class HangmanRDFImpl implements HangmanRDF {

    // Constants
    private static final String RDF_IMI = "http://intelligent-multimodal-interaction.org/relations";
    private static final String RDF_BASE = "http://intelligent-multimodal-interaction.org/concepts";

    private static final String SPARQL_PREFIX_RDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
    private static final String SPARQL_PREFIX_IMI = "PREFIX imi: <" + RDF_IMI + "/>";
    private static final String SPARQL_PREFIX_BASE = "PREFIX base: <" + RDF_BASE + "/>";
    private static final String SPARQL_PREFIX = SPARQL_PREFIX_RDF + "\n" + SPARQL_PREFIX_IMI + "\n" + SPARQL_PREFIX_BASE + "\n";

    // Attributes
    private String pathToModalityFile;
    private String pathToCurrentScenarioFile;
    private AssetManager assetManager;

    private Repository repository;

    // Constructors
    public HangmanRDFImpl(String pathToModalityFile, String pathToCurrentScenarioFile, AssetManager assetManager, boolean printRepository) {

        this.pathToModalityFile = pathToModalityFile;
        this.pathToCurrentScenarioFile = pathToCurrentScenarioFile;

        this.assetManager = assetManager;

        this.initializeRepositories(printRepository);
    }
    public HangmanRDFImpl(String pathToModalityFile, String pathToCurrentScenarioFile, AssetManager assetManager) {
        this(pathToModalityFile, pathToCurrentScenarioFile, assetManager, false);
    }
	
	/* Implementation of the HangmanRDF interface methods */

    public void setCurrentScenario(Scenario scenario) {

        ValueFactory f = this.repository.getValueFactory();

        URI currentScenario = f.createURI(RDF_BASE + "/CurrentScenario");
        URI currentHandicap = f.createURI(RDF_IMI + "/currentHandicap");
        URI currentEnvironment = f.createURI(RDF_IMI + "/currentEnvironment");

        try {
            RepositoryConnection con = this.repository.getConnection();
            try {

                // remove all statements about the current scenario
                con.remove(currentScenario, null, null);

                // add statements about the new scenario
                con.add(currentScenario, currentHandicap, f.createURI(RDF_BASE + "/" + Handicap.handicapToString(scenario.getHandicap())));
                con.add(currentScenario, currentEnvironment, f.createURI(RDF_BASE + "/" + Environment.environmentToString(scenario.getEnvironment())));

            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scenario getCurrentScenario() {

        Scenario scenario = null;

        try {
            RepositoryConnection con = this.repository.getConnection();

            try {
				/* SPARQL Queries */
				/* search current scenario */
                String queryString = SPARQL_PREFIX +
                        "SELECT ?handicap ?environment WHERE {" +
                        "base:CurrentScenario imi:currentHandicap ?handicap. " +
                        "base:CurrentScenario imi:currentEnvironment ?environment. " +
                        "}";
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = tupleQuery.evaluate();
                try {
                    if (result.hasNext()) {
                        BindingSet bindingSet = result.next();
                        Value handicapValue = bindingSet.getValue("handicap");
                        Value environmentValue = bindingSet.getValue("environment");

                        Handicap handicap = Handicap.stringToHandicap(handicapValue.toString().replace(RDF_BASE + "/", ""));
                        Environment environment = Environment.stringToEnvironment(environmentValue.toString().replace(RDF_BASE + "/", ""));

                        scenario = new Scenario(handicap, environment);
                    }
                } finally {
                    result.close();
                }
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scenario;
    }

    public void resetCurrentScenario() {
        // for now, simply reinitialize the repositories
        // the current scenario will also be reseted for the one that was in the RDF file
        initializeRepositories(false);
    }

    public void saveCurrentScenario() {
        try {
            RepositoryConnection con = this.repository.getConnection();
            try {
                RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.pathToCurrentScenarioFile), "utf-8")));

                String query = SPARQL_PREFIX +
                        "CONSTRUCT { base:CurrentScenario ?p ?o }" +
                        " WHERE {" +
                        "base:CurrentScenario ?p ?o . " +
                        "}";

                con.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate(writer);
            }
            finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Modality> getAvailableInputModalities() {

        return this.getAvailableModalities("InputModality");

    }

    public ArrayList<Modality> getAvailableOutputModalities() {

        return this.getAvailableModalities("OutputModality");

    }

    private ArrayList<Modality> getAvailableModalities(String typeOfModality) {

        ArrayList<Modality> modalities = new ArrayList<Modality>();

        try {
			/* repository */
            RepositoryConnection con = this.repository.getConnection();

            try {
				/* SPARQL Queries */
				/* search an input/output modality and its associated constraint domain */
                String queryString = SPARQL_PREFIX +
                        "SELECT DISTINCT ?modality WHERE {" +
                        "?modality rdf:type base:"+ typeOfModality + ". " +
                        "?modality imi:mode ?mode. " +
                        "MINUS {" +
                        "base:CurrentScenario imi:currentHandicap ?handicap. " +
                        "base:CurrentScenario imi:currentEnvironment ?environment. " +
                        "?handicap imi:constraint ?handicapConstraint. " +
                        "?handicapConstraint imi:mode ?disabledMode. " +
                        "?handicapConstraint imi:constraint-value 'none'. " +
                        "?modality imi:mode ?disabledMode. " +
                        "} " +
                        "MINUS {" +
                        "base:CurrentScenario imi:currentHandicap ?handicap. " +
                        "base:CurrentScenario imi:currentEnvironment ?environment. " +
                        "?environment imi:constraint ?environmentConstraint. " +
                        "?environmentConstraint imi:mode ?disabledMode. " +
                        "?environmentConstraint imi:constraint-value 'none'. " +
                        "?modality imi:mode ?disabledMode. " +
                        "} " +
                        "MINUS {" +
                        "base:CurrentScenario imi:currentHandicap ?handicap. " +
                        "base:CurrentScenario imi:currentEnvironment ?environment. " +
                        "?handicap imi:constraint ?handicapConstraint. " +
                        "?handicapConstraint imi:modality ?modality. " +
                        "?handicapConstraint imi:constraint-value 'none'. " +
                        "} " +
                        "MINUS {" +
                        "base:CurrentScenario imi:currentHandicap ?handicap. " +
                        "base:CurrentScenario imi:currentEnvironment ?environment. " +
                        "?environment imi:constraint ?environmentConstraint. " +
                        "?environmentConstraint imi:modality ?modality. " +
                        "?environmentConstraint imi:constraint-value 'none'. " +
                        "} " +
                        "}";
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = tupleQuery.evaluate();
                try {
                    while (result.hasNext()) {
                        BindingSet bindingSet = result.next();
                        Value modality = bindingSet.getValue("modality");
                        modalities.add(Modality.stringToModality(modality.toString().replace(RDF_BASE + "/", "")));
                    }
                } finally {
                    result.close();
                }
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return modalities;
    }

    private void printRepository() throws RepositoryException {

        System.out.println("\nRepository: \n");

        RepositoryConnection con = this.repository.getConnection();
        try {
            RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
            try {
                while (statements.hasNext()) {
                    System.out.println(statements.next());
                }
            } finally {
                statements.close();
            }
        } finally {
            con.close();
        }

    }

    private void initializeRepositories(boolean printRepository) {
        this.repository = new SailRepository(new MemoryStore());
        try {
            repository.initialize();
            RepositoryConnection con = this.repository.getConnection();
            try {
				/* add RDF files to Repository */
                con.add(this.assetManager.open(this.pathToModalityFile), "", RDFFormat.RDFXML);			    // Modalities
                con.add(this.assetManager.open(this.pathToCurrentScenarioFile), "", RDFFormat.RDFXML);	// Current Scenario

                if (printRepository)
                    this.printRepository();

            } finally {
                con.close();	// always close the connection
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
