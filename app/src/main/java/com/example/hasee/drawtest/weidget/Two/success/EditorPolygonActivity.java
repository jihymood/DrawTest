package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.PoPoListModel;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditorPolygonActivity extends AppCompatActivity {

    @Bind(R.id.bnt_draw_new)
    Button bntDrawNew;
    @Bind(R.id.iv_clear)
    ImageView ivClear;
    @Bind(R.id.et_line_long)
    EditText etLineLong;
    @Bind(R.id.bnt_ok_long)
    Button bntOkLong;
    @Bind(R.id.ll_line_set)
    LinearLayout llLineSet;
    @Bind(R.id.magicPlan)
    MagicPlanEditorView magicPlan;
    private List<PoPoListModel> polygons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_polygon);
        ButterKnife.bind(this);
        magicPlan.setListener(new MagicPlanEditorView.IShowChangeLongViewListener() {
            @Override
            public void showView(String lineLong) {
                etLineLong.setText(lineLong);
                ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1);
                animation.setDuration(500);
                llLineSet.setAnimation(animation);
                llLineSet.setVisibility(View.VISIBLE);
                bntDrawNew.setVisibility(View.GONE);
            }

            @Override
            public void cancelView() {
                llLineSet.setVisibility(View.GONE);
                bntDrawNew.setVisibility(View.VISIBLE);
            }
        });
        polygons = (List<PoPoListModel>) getIntent().getSerializableExtra("polygons");
        magicPlan.setPoints(polygons);
        float scale = getIntent().getFloatExtra("scale", 1f);
        magicPlan.setmScale(scale);
        bntDrawNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorPolygonActivity.this, ThreeActivity.class);
                intent.putExtra("polygons", (Serializable) magicPlan.getPoints());
                intent.putExtra("scale", magicPlan.getmScale());
                startActivity(intent);
                finish();

            }
        });
        bntOkLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String longNum = etLineLong.getText().toString();
                if (longNum == null || longNum.equals("")) {
                    Toast.makeText(EditorPolygonActivity.this, "请填写有效数值", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    magicPlan.setLongNum(Double.valueOf(longNum));
                    ScaleAnimation animation = new ScaleAnimation(1, 1, 1, 0);
                    animation.setDuration(500);
                    llLineSet.setAnimation(animation);
                    llLineSet.setVisibility(View.GONE);
                    bntDrawNew.setVisibility(View.VISIBLE);
                }
            }
        });
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLineLong.setText("");
            }
        });
    }
}
