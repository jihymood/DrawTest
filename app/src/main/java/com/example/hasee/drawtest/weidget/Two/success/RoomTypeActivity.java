package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hasee.drawtest.R;


public class RoomTypeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private EditText editText,beizhuText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_type);

        init();

    }


    public void init() {
        button = (Button) findViewById(R.id.saveBtn);
        editText = (EditText) findViewById(R.id.roomType1);
        beizhuText = (EditText) findViewById(R.id.beizhu1);


        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveBtn:

                Intent intent = new Intent();
                intent.putExtra("editText", editText.getText().toString());
                intent.putExtra("beizhuText", beizhuText.getText().toString());

                setResult(2, intent);
                this.finish();
                break;
        }
    }
}
