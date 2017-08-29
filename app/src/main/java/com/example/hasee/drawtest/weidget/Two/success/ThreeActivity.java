package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.PoPoListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button completeBtn, restoreBtn, polygonalLineBtn, squareBtn;
    private MagicPlanDrawView magicPlanView;
    private List<PoPoListModel> polygons;
    private RelativeLayout relativeLayout;

    private int height, width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_line);

        init();
        getWH();
        getFromIntent();

    }

    public void init() {
        completeBtn = (Button) findViewById(R.id.completeBtn);
        restoreBtn = (Button) findViewById(R.id.restoreBtn);
        squareBtn = (Button) findViewById(R.id.squareBtn);
        polygonalLineBtn = (Button) findViewById(R.id.polygonalLineBtn);
        magicPlanView = (MagicPlanDrawView) findViewById(R.id.magicPlan_View);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        completeBtn.setOnClickListener(this);
        restoreBtn.setOnClickListener(this);
        squareBtn.setOnClickListener(this);
        polygonalLineBtn.setOnClickListener(this);

        polygons = new ArrayList<>();
    }

    /**
     * 获得控件高度
     */
    public void getWH() {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        relativeLayout.measure(w, h);
        width = relativeLayout.getMeasuredWidth();
        height = relativeLayout.getMeasuredHeight();
        Log.e("ThreeActivity", "width:" + width + "/height:" + height);
    }

    public void getFromIntent() {
        List<PoPoListModel> polygons = (List<PoPoListModel>) getIntent().getSerializableExtra("polygons");
        List<BaseView.ImageGroup> imageGroups = (List<BaseView.ImageGroup>)getIntent().getSerializableExtra
                ("decalImages");
        if (polygons != null) {
            magicPlanView.setShowPolygons(polygons);
        }
        if (imageGroups != null) {
            magicPlanView.setImageGroups(imageGroups);
        }
        float curScale = getIntent().getFloatExtra("scale", 1f);
        magicPlanView.setmScale(curScale);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completeBtn:  //完成
                if (magicPlanView.closeView()) {
                    List<PoPoListModel> polygons = magicPlanView.getShowPolygons();
                    if (polygons.size() > 0 && polygons != null) {
                        Intent intent = new Intent(ThreeActivity.this, FourActivity.class);
                        intent.putExtra("polygons", (Serializable) polygons);
                        intent.putExtra("scale", magicPlanView.getmScale());
                        startActivity(intent);
                        this.finish();
                    } else {
                        Toast.makeText(ThreeActivity.this, "无法完成绘制请调整抬手点", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ThreeActivity.this, "图形不符合要求,请重新绘制", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.restoreBtn: //复原
                magicPlanView.recover();
                break;
            case R.id.squareBtn: //正方形可绘制，折线不可绘制
                magicPlanView.addSquare(true,true,false);

                break;
            case R.id.polygonalLineBtn: //折线可绘制，正方形不可绘制
                magicPlanView.addpolygonalLine(true,false);

                break;

            default:
                break;
        }
    }
}
