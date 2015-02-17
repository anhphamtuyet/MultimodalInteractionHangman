package projectparissud.multimodalhangman;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;
import android.os.Vibrator;
import android.media.MediaPlayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;


public class GameActivity extends ActionBarActivity {
    String wordToGuess;
    String guessed;
    String triedLetters = "";
    int score = 10;

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
        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        if(won) {
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
    
    public void playAudio(boolean won, boolean lost, boolean found){
        int sound;
        if(won) {
            sound = R.raw.win;
        } else if (lost) {
            sound = R.raw.lost;
        } else if (found) {
            sound = R.raw.found;
        } else { // Not found
            sound = R.raw.notfound;
        }
        MediaPlayer mp = MediaPlayer.create(this, sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.wordToGuess = this.chooseRandomWord();
        this.guessed = this.initGuessed();
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
     * A function to check the letter sent by the user when he/she validates it by clicking the button
     * @param view The current view
     */
    public void onClick(View view){
        EditText input = (EditText) findViewById(R.id.main_input);
        String string = input.getText().toString();
        if(string.length() != 1){
            Toast.makeText(this, "Enter one letter", Toast.LENGTH_LONG);
            return;
        }
        char letter = string.charAt(0);
        if (Character.isLetter(letter)) {
            this.CheckLetter(letter);
        } else {
            Toast.makeText(this, "You have to input a valid letter (a-z)", Toast.LENGTH_LONG);
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
}
