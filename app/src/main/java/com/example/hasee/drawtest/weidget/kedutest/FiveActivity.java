package com.example.hasee.drawtest.weidget.kedutest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.hasee.drawtest.R;

public class FiveActivity extends AppCompatActivity {

    private ReferenceView referenceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);

        referenceView = (ReferenceView) findViewById(R.id.reference_view);

    }
}
