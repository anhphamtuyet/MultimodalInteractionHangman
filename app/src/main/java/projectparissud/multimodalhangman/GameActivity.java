package projectparissud.multimodalhangman;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;
import android.os.Vibrator;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Locale;

import projectparissud.multimodalhangman.rdf.HangmanRDF;
import projectparissud.multimodalhangman.rdf.HangmanRDFImpl;
import projectparissud.multimodalhangman.scenario.Environment;
import projectparissud.multimodalhangman.scenario.Handicap;
import projectparissud.multimodalhangman.scenario.Modality;
import projectparissud.multimodalhangman.scenario.Scenario;


public class GameActivity extends ActionBarActivity implements OnInitListener {

    private static final int REQ_CODE_SPEECH_SYNTHESIS = 100;
    private static final int REQ_CODE_SPEECH_RECOGNITION = 200;

    String wordToGuess;
    String guessed;
    String triedLetters = "";
    int score = 10;

    // RDF related attributes
    private HangmanRDF rdf;
    private ArrayList<Modality> availableInputModalities;
    private ArrayList<Modality> availableOutputModalities;

    // TextToSpeech Engine
    private TextToSpeech gameTTS;

    public String initGuessed(){
        String result = "";
        for(int i = 0; i < this.wordToGuess.length(); i++){
            result += "_";
        }
        return result;
    }

    /**
     * Function extracting a random word from the built-in dictionary
     * @return A string matching the chosen word
     */
    public String chooseRandomWord(){
        String result = "";
        AssetManager am = this.getAssets();
        InputStream is;
        try {
            is = am.open("english-words.txt");
            // The dictionary is quite big, we implement a reservoir sampling to extract one line
            Random rand = new Random();
            int n = 0;
            for(Scanner sc = new Scanner(is); sc.hasNext(); )
            {
                ++n;
                String line = sc.nextLine();
                if(rand.nextInt(n) == 0)
                    result = line;
            }
            return result.toUpperCase();
        } catch (IOException e) {
            Toast.makeText(this, "Error : unable to open dictionary", Toast.LENGTH_LONG);
            return null;
        }
    }

    /**
     * A function to check if the letter sent by the user has already been tried before
     * @param  letter A character sent by the user
     * @return A boolean that says "true" if the letter was already tried before
     */
    public boolean hasTried(char letter) {
        int i = 0;
        boolean result = false;
        while(!result && i < triedLetters.length()) {
            result = Character.toLowerCase(triedLetters.charAt(i)) == Character.toLowerCase(letter);
            i++;
        }
        return result;
    }

    /**
     * A function to check if the letter in input appears in the word to guess.
     * If this is the case, we make the occurrences of the letter appear.
     * @param letter A character sent by the user
     */
    // Function confronting the letter in input against the word
    public void CheckLetter(char letter){
        boolean tried = this.hasTried(letter);
        if(!tried && score > 0) {
            this.triedLetters += letter;
            boolean found = false;
            char[] guessedChars = this.guessed.toCharArray();
            for (int i = 0; i < wordToGuess.length(); i++) {
                if (Character.toLowerCase(this.wordToGuess.charAt(i)) == Character.toLowerCase(letter)) {
                    found = true;
                    guessedChars[i] = Character.toUpperCase(letter);
                }
            }
            this.guessed = String.valueOf(guessedChars);

            // If the letter was not found in the word, decrease the score
            if (!found) {
                this.score--;
            }

            this.updateView(found);
        } else if(tried) {
            Toast.makeText(this, "You already tried this letter", Toast.LENGTH_LONG);
        }
    }

    /**
     * The function called to update the view after modifying the letters tried, the current state of the word and the number of tries
     * @param found Boolean to indicate if the letter in input was found
     */
    public void updateView(boolean found){
        TextView output = (TextView) findViewById(R.id.bigTextView);
        // We add dots between each underscore to allow the user to count the letters
        String guessedOutput = "";
        for(int i = 0; i < this.guessed.length() - 1; i++){
            guessedOutput += this.guessed.charAt(i) + ".";
        }
        guessedOutput += this.guessed.charAt(this.guessed.length() - 1);
        output.setText(guessedOutput);
        TextView tried = (TextView) findViewById(R.id.triedLettersView);
        tried.setText(this.triedLetters);
        TextView score = (TextView) findViewById(R.id.scoreView);
        score.setText(Integer.toString(this.score));
        EditText input = (EditText) findViewById(R.id.main_input);
        input.setText("");
        HangmanCanvasView canvas = (HangmanCanvasView) findViewById(R.id.hangman_canvas_view);
        canvas.drawHangman(this.score);

        boolean lost = this.score == 0;
        if(lost){
            Toast.makeText(this, "You lost !", Toast.LENGTH_SHORT).show();
            output.setText(this.wordToGuess);
        }

        boolean won = this.guessed.equals(this.wordToGuess);
        if(won){
            Toast.makeText(this, "You won !", Toast.LENGTH_LONG).show();
        }

        this.vibrate(won, lost, found);
        this.playAudio(won, lost, found);
    }

    public void vibrate(boolean won, boolean lost, boolean found){
        if (this.availableOutputModalities.contains(Modality.VIBRATION)) {
            Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            if (won) {
                // Vibrate one time 1s
                vibrator.vibrate(1000);
            } else if (lost) {
                // Vibrate three times 500ms
                long[] pattern = {0, 500, 200, 500, 200, 500};
                vibrator.vibrate(pattern, -1);
            } else if (found) {
                // Letter was found, vibrate 200ms
                vibrator.vibrate(200);
            } else {
                // Not found, vibrate two times 200ms
                long[] pattern = {0, 200, 200, 200};
                vibrator.vibrate(pattern, -1);
            }
        }
    }
    
    public void playAudio(boolean won, boolean lost, boolean found){
        if (this.availableOutputModalities.contains(Modality.EARCON)) {
            int sound;
            if (won) {
                sound = R.raw.win;
            } else if (lost) {
                sound = R.raw.lost;
            } else if (found) {
                sound = R.raw.found;
            } else { // Not found
                sound = R.raw.notfound;
            }
            final boolean speechWon = won;
            final boolean speechLost = lost;
            MediaPlayer mp = MediaPlayer.create(this, sound);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    playTTS(speechWon, speechLost);
                }
            });
            mp.start();
        }
    }

    public void playTTS(boolean won, boolean lost){
        if (this.gameTTS != null && this.availableOutputModalities.contains(Modality.SPEECH_SYNTHESIZER)) {
            if (won || lost) {
                this.gameTTS.speak(this.wordToGuess, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                for(int i = 0; i < this.guessed.length(); i++){
                    char character = this.guessed.charAt(i);
                    if(character == '_') {
                        this.gameTTS.speak("Unknown", TextToSpeech.QUEUE_ADD, null);
                    } else {
                        System.out.println(character);
                        this.gameTTS.speak(this.wordToGuess.charAt(i) + "", TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        }
    }

    private void getModalities() {
        this.availableInputModalities = this.rdf.getAvailableInputModalities();
        this.availableOutputModalities = this.rdf.getAvailableOutputModalities();
    }

    private void spellWord(String word){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.wordToGuess = this.chooseRandomWord();
        this.guessed = this.initGuessed();

        Intent intent = getIntent();

        // get available modalities
        this.rdf = new HangmanRDFImpl("rdf/hangman.rdf", "rdf/currentScenario.rdf", this.getAssets());
        this.rdf.setCurrentScenario(new Scenario(Handicap.stringToHandicap(intent.getStringExtra("handicap")),
                                                    Environment.stringToEnvironment(intent.getStringExtra("environment"))));
        this.getModalities();

        System.out.println("Scenario: " + this.rdf.getCurrentScenario().getHandicap() + " & " +
                this.rdf.getCurrentScenario().getEnvironment());
        System.out.print("Available Input Modalities: ");
        System.out.println(availableInputModalities);
        System.out.print("Available Output Modalities: ");
        System.out.println(availableOutputModalities);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, REQ_CODE_SPEECH_SYNTHESIS);

        // watch for changes in the input
        EditText input = (EditText) findViewById(R.id.main_input);
        input.addTextChangedListener(new TextWatcher() {

            private EditText input = (EditText) findViewById(R.id.main_input);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (Character.isLetter(s.charAt(0))) {
                        CheckLetter(s.charAt(0));
                    } else {
                        Toast.makeText(getApplicationContext(), "You have to input a valid letter (a-z)", Toast.LENGTH_LONG);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                s.clear();
            }
        });
    }

    public void onInit(int initStatus) {
        if (gameTTS != null && initStatus == TextToSpeech.SUCCESS) {
            if (gameTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
                gameTTS.setLanguage(Locale.US);
            }
        }
    }

    @Override
    public void onDestroy(){
        if (gameTTS != null) {
            gameTTS.stop();
            gameTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A function guess a letter.
     * It chooses the appropriate input modality, based on the available
     * input modalities.
     * @param view The current view
     */
    public void onClick(View view){
        if (!this.guessed.equals(this.wordToGuess) && this.score > 0) {
            EditText input = (EditText) findViewById(R.id.main_input);

            if (this.availableInputModalities.contains(Modality.KEYBOARD)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

            } else if (this.availableInputModalities.contains(Modality.SPEECH_RECOGNITION)) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_prompt));
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_RECOGNITION);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_not_supported),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Game already finished. Tap the RESET button to start another game.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A function to reset the view when the user clicks on the reset button
     * It clears the word to guess, the score and the letters already tried
     * @param view The current view
     */
    public void onClear(View view){
        this.wordToGuess = this.chooseRandomWord();
        this.guessed = this.initGuessed();
        this.triedLetters = "";
        this.score = 10;

        this.updateView(false);
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_RECOGNITION: {
                if (resultCode == RESULT_OK && null != data) {
                    EditText input = (EditText) findViewById(R.id.main_input);

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    input.setText(result.get(0));
                }
                break;
            }
            case REQ_CODE_SPEECH_SYNTHESIS: {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    gameTTS = new TextToSpeech(this, this);
                }
                else {
                    gameTTS = null;
                }
                break;
            }

        }
    }
}
