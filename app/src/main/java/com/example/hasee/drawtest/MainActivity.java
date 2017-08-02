package com.example.hasee.drawtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hasee.drawtest.weidget.One.DrawPolygonView;
import com.example.hasee.drawtest.weidget.One.DrawPolygonView1;
import com.example.hasee.drawtest.weidget.One.DrawTestView_san;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button clear,tiaozhuan1,tiaozhuan2,tiaozhuan3;
    private DrawPolygonView drawPolygonView;
    private DrawPolygonView1 drawPloygonView1;
    private DrawTestView_san drawTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = (Button) findViewById(R.id.clear);
        tiaozhuan1 = (Button) findViewById(R.id.tiaozhuan1);
        tiaozhuan2 = (Button) findViewById(R.id.tiaozhuan2);
        tiaozhuan3 = (Button) findViewById(R.id.tiaozhuan3);
        drawPolygonView = (DrawPolygonView) findViewById(R.id.drawPloygonView);
        drawTestView = (DrawTestView_san) findViewById(R.id.drawTestView);
        drawPloygonView1 = (DrawPolygonView1) findViewById(R.id.drawPloygonView1);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawPolygonView.cleanDraw();
                drawTestView.cleanDraw();
                drawPloygonView1.cleanDraw();
            }
        });

        tiaozhuan1.setOnClickListener(this);
        tiaozhuan2.setOnClickListener(this);
        tiaozhuan3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tiaozhuan1:
                startActivity(new Intent(MainActivity.this, FirstActivity.class));
                break;
            case R.id.tiaozhuan2:
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                break;
            case R.id.tiaozhuan3:
                startActivity(new Intent(MainActivity.this, ThreeActivity.class));
                break;
        }
    }
}
