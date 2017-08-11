package com.example.hasee.drawtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.example.hasee.drawtest.adapter.RecyclerViewAdapter;
import com.example.hasee.drawtest.model.GridSpacingItemDecoration;
import com.example.hasee.drawtest.model.ViewListModel;
import com.example.hasee.drawtest.model.ViewModel;
import com.example.hasee.drawtest.weidget.Two.success.ThreeActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main2Activity extends AppCompatActivity {

    @Bind(R.id.addDrawBtn)
    Button addDrawBtn;
    @Bind(R.id.recycleView)
    RecyclerView recycleView;
    private RecyclerViewAdapter recyclerViewAdapter;
    //    private List<ViewModel> viewModelList;
    private ViewModel viewModel;
    private List<ViewModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        initData();

        recyclerViewAdapter = new RecyclerViewAdapter(list, this);
//        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
//        recycleView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        int spanCount = 3;
        int spacing = 50;
        boolean includeEdge = false;
        recycleView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recycleView.setLayoutManager(new GridLayoutManager(this, 2));
        recycleView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick() {
                Toast.makeText(Main2Activity.this, "点我的", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 获取数据
     */
    public void initData() {
//        PointListModel pointListModel = new PointListModel();
//        listModels = pointListModel.getListModels();

//        viewModelList = new ArrayList<>();
//        ViewModel viewModel = new ViewModel();
//        viewModel.setView(new LinearLayout(this));
//        viewModelList.add(viewModel);

//        viewModelList = new ArrayList<>();
//        viewModelList.add( )

//        ViewModel viewModel=ViewModel.getInstance();
//        viewModelList = viewModel.getViewList();


//        viewModelList = new ArrayList<>();
//        PointListModel pointListModel = new PointListModel();
//        poListModelList = pointListModel.getListModels();

//        viewModel=ViewModel.getInstance();
//        viewModel


        ViewListModel viewListModel = ViewListModel.getInstance();
        list = viewListModel.getPointAndBitmapList();


    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        recyclerViewAdapter.notifyDataChanged(list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果是返回的标识
            //获取数据

            list = (List<ViewModel>) data.getSerializableExtra("list");
            recyclerViewAdapter.addList(list);
            recyclerViewAdapter.notifyDataSetChanged();

        }
    }


    @OnClick(R.id.addDrawBtn)
    public void onViewClicked() {
        Intent intent = new Intent(Main2Activity.this, ThreeActivity.class);
        startActivityForResult(intent, 0);
//        finish();
    }
}
