package projectparissud.multimodalhangman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import projectparissud.multimodalhangman.scenario.Environment;
import projectparissud.multimodalhangman.scenario.Handicap;


public class StartActivity extends ActionBarActivity {

    Spinner spinnerHandicap;
    Spinner spinnerEnvironment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Fill the spinners
        // Handicap Spinner
        ArrayAdapter<CharSequence> adapterHandicap =
                new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
        adapterHandicap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Handicap handicap : Handicap.values()) {
            if (handicap != Handicap.UNKNOWN)
                adapterHandicap.add(Handicap.handicapToString(handicap));
        }

        spinnerHandicap = (Spinner) findViewById(R.id.handicap_spinner);
        spinnerHandicap.setAdapter(adapterHandicap);

        // Environment Spinner
        ArrayAdapter<CharSequence> adapterEnvironment =
                new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
        adapterEnvironment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Environment environment : Environment.values()) {
            if (environment != Environment.UNKNOWN)
                adapterEnvironment.add(Environment.environmentToString(environment));
        }

        spinnerEnvironment = (Spinner) findViewById(R.id.environment_spinner);
        spinnerEnvironment.setAdapter(adapterEnvironment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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


    public void onClick(View view){
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);

        intent.putExtra("handicap", this.spinnerHandicap.getSelectedItem().toString());
        intent.putExtra("environment", this.spinnerEnvironment.getSelectedItem().toString());

        startActivity(intent);
    }
}
