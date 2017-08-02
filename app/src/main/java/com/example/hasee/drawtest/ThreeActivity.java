package com.example.hasee.drawtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.PointListModel;
import com.example.hasee.drawtest.weidget.Two.MagicPlanView2;

import java.io.Serializable;
import java.util.List;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button close, completeBtn;
    private MagicPlanView2 magicPlanView;
    private List<Point> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        close = (Button) findViewById(R.id.close);
        completeBtn = (Button) findViewById(R.id.completeBtn);
        magicPlanView = (MagicPlanView2) findViewById(R.id.magicPlan_View);

        close.setOnClickListener(this);
        completeBtn.setOnClickListener(this);
        close.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.close:
//                magicPlanView.closeView();
//                Toast.makeText(ThreeActivity.this, "magicPlanView.getPoints():" + magicPlanView.getPoints().size(),
//                        Toast.LENGTH_SHORT).show();
//
//                points = magicPlanView.getPoints();
//                PointListModel pointListModel = PointListModel.getInstance();
//                pointListModel.addList(points);
//
//                break;
//            case R.id.completeBtn:
////                points = magicPlanView.getPoints();
////                PointListModel pointListModel = PointListModel.getInstance();
////                pointListModel.addList(points);
////                Log.e("ThreeActivity", "pointListModel.getList().size():" + pointListModel.getList().size());
//                magicPlanView.setDrawAgain();
//                break;

            case R.id.close:
                magicPlanView.closeView();
                Toast.makeText(ThreeActivity.this, "magicPlanView.getPoints():" + magicPlanView.getPoints().size(),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.completeBtn:
                points = magicPlanView.getPoints();
                PointListModel pointListModel = PointListModel.getInstance();
                pointListModel.addList(points);
                Log.e("ThreeActivity", "pointListModel.getList().size():" + pointListModel.getList().size());

                Intent intent = new Intent(ThreeActivity.this, FourActivity.class);
                intent.putExtra("pointList", (Serializable) points);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
