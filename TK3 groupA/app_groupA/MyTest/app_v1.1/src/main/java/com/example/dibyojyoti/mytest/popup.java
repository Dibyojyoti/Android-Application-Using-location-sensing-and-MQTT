package com.example.dibyojyoti.mytest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Displaying message details.
 */
public class popup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("messageText");
        EditText editText = (EditText) findViewById(R.id.pop_msg);
        editText.setText(message);
        editText.setEnabled(false);
        Button closeButton = (Button)findViewById(R.id.pop_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        getWindow().setLayout((int)(width*0.8),(int)(height*0.7));
    }
}
