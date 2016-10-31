package com.hue.helloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.hue.helloworld.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* called when the button 'I want something' is clicked */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText edtText = (EditText) findViewById(R.id.edtText);
        String message = edtText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /* called when the button 'Show me map' is clicked */
    public void showMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        EditText edtText = (EditText) findViewById(R.id.edtText);
        String message = edtText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
