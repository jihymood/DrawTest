package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FourActivity extends AppCompatActivity implements View.OnClickListener {

    private static List<Point> getFromPointList;
    private List<List<Point>> twofoldList;
    private MyDrawView drawView;
    private Button addDrawBtn;
    private List<PoPoListModel> polygons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        addDrawBtn = (Button) findViewById(R.id.addDrawBtn);
        drawView = (MyDrawView) findViewById(R.id.myDraw_view);
        addDrawBtn.setOnClickListener(this);

//        PointListModel pointListModel = PointListModel.getInstance();
//        List<List<Point>> list = pointListModel.getList();
//        drawView.setAllList(list);
//        getList();


        twofoldList = new ArrayList<>();
        polygons = (List<PoPoListModel>) getIntent().getSerializableExtra("polygons");
        for (PoPoListModel polygon : polygons) {
            List<Point> list = polygon.getList();
            twofoldList.add(list);
        }
        drawView.setAllList(twofoldList);
        float scale = getIntent().getFloatExtra("scale", 1f);
        drawView.setmScale(scale);



    }

    public List<Point> getList() {
        Intent intent = getIntent();
        getFromPointList = (List<Point>) intent.getSerializableExtra("pointList");
        float scale = intent.getFloatExtra("scale",1f);
        drawView.setmScale(scale);
        Toast.makeText(this, "getFromPointList.size():" + getFromPointList.size(), Toast.LENGTH_SHORT).show();
        return getFromPointList;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDrawBtn:
                Intent intent = new Intent(this, ThreeActivity.class);
                intent.putExtra("polygons", (Serializable) drawView.getTwofoldList());
                intent.putExtra("scale", drawView.getmScale());
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
