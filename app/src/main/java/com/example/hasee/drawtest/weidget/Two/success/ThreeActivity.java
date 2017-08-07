package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.PointListModel;

import java.io.Serializable;
import java.util.List;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button close, completeBtn;
    private MagicPlanDrawView magicPlanView;
//    private MagicPlanView2 magicPlanView;
    private List<Point> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        close = (Button) findViewById(R.id.close);
        completeBtn = (Button) findViewById(R.id.completeBtn);
        magicPlanView = (MagicPlanDrawView) findViewById(R.id.magicPlan_View);

        close.setOnClickListener(this);
        completeBtn.setOnClickListener(this);
        close.setOnClickListener(this);


        magicPlanView.closeView();
        points = magicPlanView.getMovePoints();
        PointListModel pointListModel = PointListModel.getInstance();
        pointListModel.addList(points);
        Log.e("ThreeActivity", "pointListModel.getList().size():" + pointListModel.getList().size());


//        List<List<Point>> list = pointListModel.getList();
//        List<PoPoListModel> poPoListModels = new ArrayList<>();
//        PoPoListModel poListModel = new PoPoListModel();
//        for (List<Point> pointList : list) {
//            poListModel.set
//            poPoListModels.add(pointList);
//        }
//        magicPlanView.setShowPolygons(list);


        Intent intent = getIntent();
        if (intent!= null) {
            float scale = intent.getFloatExtra("scale",1f);
            magicPlanView.setmScale(scale);
        }



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
                Toast.makeText(ThreeActivity.this, "magicPlanView.getPoints():" + magicPlanView.getMovePoints().size(),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.completeBtn:

                Intent intent = new Intent(ThreeActivity.this, FourActivity.class);
                intent.putExtra("pointList", (Serializable) points);
                intent.putExtra("scale", magicPlanView.getmScale());
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
