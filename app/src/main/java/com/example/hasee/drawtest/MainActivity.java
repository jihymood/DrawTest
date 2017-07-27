package com.example.hasee.drawtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hasee.drawtest.weidget.DrawPolygonView;
import com.example.hasee.drawtest.weidget.DrawPolygonView1;
import com.example.hasee.drawtest.weidget.DrawTestView_san;

public class MainActivity extends AppCompatActivity {

    private Button clear,tiaozhuan;
    private DrawPolygonView drawPolygonView;
    private DrawPolygonView1 drawPloygonView1;
    private DrawTestView_san drawTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = (Button) findViewById(R.id.clear);
        tiaozhuan = (Button) findViewById(R.id.tiaozhuan);
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

        tiaozhuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FirstActivity.class));

            }
        });
    }
}
