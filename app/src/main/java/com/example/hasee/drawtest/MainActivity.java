package com.example.hasee.drawtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hasee.drawtest.weidget.DrawPolygonView;
import com.example.hasee.drawtest.weidget.DrawTestView_san;

public class MainActivity extends AppCompatActivity {

    private Button clear;
    private DrawPolygonView drawPolygonView;
    private DrawTestView_san drawTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = (Button) findViewById(R.id.clear);
        drawPolygonView = (DrawPolygonView) findViewById(R.id.drawPloygonView);
        drawTestView = (DrawTestView_san) findViewById(R.id.drawTestView);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DrawTestView_san san = new DrawTestView_san(MainActivity.this);
//                DrawPolygonView drawPolygonView = new DrawPolygonView(MainActivity.this);
                drawPolygonView.cleanDraw();
                drawTestView.cleanDraw();
            }
        });
    }
}
