package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.PoPoListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button completeBtn;
    private MagicPlanDrawView magicPlanView;
    private List<PoPoListModel> polygons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        completeBtn = (Button) findViewById(R.id.completeBtn);
        magicPlanView = (MagicPlanDrawView) findViewById(R.id.magicPlan_View);

        completeBtn.setOnClickListener(this);


        polygons = new ArrayList<>();
        List<PoPoListModel> polygons = (List<PoPoListModel>) getIntent().getSerializableExtra("polygons");
        if (polygons != null) {
            magicPlanView.setShowPolygons(polygons);
        }
        float curScale = getIntent().getFloatExtra("scale", 1f);
        magicPlanView.setmScale(curScale);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completeBtn:
                if (magicPlanView.closeView()) {
                    List<PoPoListModel> polygons = magicPlanView.getShowPolygons();
                    if (polygons.size() > 0 && polygons != null) {
//                        Intent intent = new Intent(ThreeActivity.this, EditorPolygonActivity.class);
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
            default:
                break;
        }
    }
}
