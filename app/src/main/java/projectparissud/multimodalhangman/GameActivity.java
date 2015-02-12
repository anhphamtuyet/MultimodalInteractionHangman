package projectparissud.multimodalhangman;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends ActionBarActivity {
    // TODO : allow the user to provide its own word OR use a random word in a dictionnary
    String wordToGuess = "SUPERMAN";
    String guessed = "________";
    String triedLetters = "";
    int score = 10;

    /**
     * A function to check if the letter sent by the user has already been tried before
     * @param  letter A character sent by the user
     * @return A boolean that says "true" if the letter was already tried before
     */
    public boolean hasTried(char letter) {
        int i = 0;
        boolean result = false;
        while(!result && i < triedLetters.length()) {
            result = triedLetters.charAt(i) == letter;
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
        if(!this.hasTried(letter) && score > 0) {
            this.triedLetters += letter;
            boolean found = false;
            char[] guessedChars = this.guessed.toCharArray();
            for (int i = 0; i < wordToGuess.length(); i++) {
                if (this.wordToGuess.charAt(i) == letter) {
                    found = true;
                    guessedChars[i] = letter;
                }
            }
            this.guessed = String.valueOf(guessedChars);

            // If the letter was not found in the word, decrease the score
            if(!found){
                this.score--;
            }

            this.updateView();

            if(this.guessed.equals(this.wordToGuess)){
                Toast.makeText(this, "You won !", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * The function called to update the view after modifying the letters tried, the current state of the word and the number of tries
     */
    public void updateView(){
        TextView output = (TextView) findViewById(R.id.bigTextView);
        output.setText(this.guessed);
        TextView tried = (TextView) findViewById(R.id.triedLettersView);
        tried.setText(this.triedLetters);
        TextView score = (TextView) findViewById(R.id.scoreView);
        score.setText(Integer.toString(this.score));
        HangmanCanvasView canvas = (HangmanCanvasView) findViewById(R.id.hangman_canvas_view);
        canvas.drawHangman(this.score);
        if(this.score == 0){
            Toast.makeText(this, "You lost !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
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
     * If there are more than one character, we take the first one
     * @param view
     */
    public void onClick(View view){
        EditText input = (EditText) findViewById(R.id.main_input);
        String string = input.getText().toString();
        this.CheckLetter(string.charAt(0));
    }

    /**
     * A function to reset the view when the user clicks on the reset button
     * It clears the word to guess, the score and the letters already tried
     * @param view
     */
    public void onClear(View view){
        for(int i = 0; i < this.wordToGuess.length(); i++){
            this.guessed = "__________";
        }
        this.triedLetters = "";
        this.score = 10;

        this.updateView();
    }
}
