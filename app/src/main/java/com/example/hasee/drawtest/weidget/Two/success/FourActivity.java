package com.example.hasee.drawtest.weidget.Two.success;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hasee.drawtest.Main2Activity;
import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.ImageGroupModel;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.ViewListModel;
import com.example.hasee.drawtest.model.ViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FourActivity extends AppCompatActivity implements View.OnClickListener, Handler.Callback {

    private Button bntOkLong;
    private ImageView amplifierBtn, homeplugBtn, routerBtn, testPointBtn, wifiBtn;
    private ImageView ivClear;
    private EditText etLineLong;
    private LinearLayout llLineSet, linearLayout;
    private RelativeLayout relativeLayout;
    private List<List<Point>> twofoldList;
    private MyDrawView drawView;
    private Button addDrawBtn, saveDrawBtn;
    private List<PoPoListModel> polygons;
    private List<ViewModel> viewModelList;
    private Handler mHandler;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private ProgressDialog mSaveProgressDlg;
    private final static int REQUESTCODE = 1; // 返回的结果码

    private CropView mCropView;
    private Bitmap routerIcon, amplifierIcon, testPointIcon, homeplugIcon, wifiIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        init();
        getFromIntent();
        setListener();

    }

    public void init() {
        addDrawBtn = (Button) findViewById(R.id.addDrawBtn);
        saveDrawBtn = (Button) findViewById(R.id.saveDrawBtn);
        bntOkLong = (Button) findViewById(R.id.bnt_ok_long);
        ivClear = (ImageView) findViewById(R.id.iv_clear);
        etLineLong = (EditText) findViewById(R.id.et_line_long);
        llLineSet = (LinearLayout) findViewById(R.id.ll_line_set);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        bntOkLong = (Button) findViewById(R.id.bnt_ok_long);
        drawView = (MyDrawView) findViewById(R.id.myDraw_view);
        amplifierBtn = (ImageView) findViewById(R.id.amplifierBtn);
        routerBtn = (ImageView) findViewById(R.id.routerBtn);
        homeplugBtn = (ImageView) findViewById(R.id.homeplugBtn);
        testPointBtn = (ImageView) findViewById(R.id.testPointBtn);
        wifiBtn = (ImageView) findViewById(R.id.wifiBtn);
        mCropView = (CropView) findViewById(R.id.main_crop_view);

        addDrawBtn.setOnClickListener(this);
        saveDrawBtn.setOnClickListener(this);
        bntOkLong.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        amplifierBtn.setOnClickListener(this);
        routerBtn.setOnClickListener(this);
        homeplugBtn.setOnClickListener(this);
        testPointBtn.setOnClickListener(this);
        wifiBtn.setOnClickListener(this);

        mHandler = new Handler(this);
        viewModelList = new ArrayList<>();
        twofoldList = new ArrayList<>();

    }

    public void getFromIntent() {
        polygons = (List<PoPoListModel>) getIntent().getSerializableExtra("polygons");
        drawView.setAllModelList(polygons);
        float scale = getIntent().getFloatExtra("scale", 1f);
        drawView.setmScale(scale);
    }


    public void setListener() {
        drawView.setListener(new MyDrawView.IShowChangeLongViewListener() {
            @Override
            public void showView(String lineLong) {
                etLineLong.setText(lineLong);
                ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1);
                animation.setDuration(500);
                llLineSet.setAnimation(animation);
                llLineSet.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public void cancelView() {
                llLineSet.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });


        drawView.setOnDoubleClickListener(new MyDrawView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                Toast.makeText(FourActivity.this, "double click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FourActivity.this, RoomTypeActivity.class);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
    }

    private void initSaveProgressDlg() {
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }


    /**
     * 得到矩阵的9个值
     * @param imageGroup
     * @return
     */
    protected float[] getValuess(BaseView.ImageGroup imageGroup) {
        return getValuess(imageGroup.bitmap, imageGroup.matrix);
    }

    protected float[] getValuess(Bitmap bitmap, Matrix matrix) {
        float[] dst = new float[9];
        float[] src = new float[]{
                0, 0,
                bitmap.getWidth(), 0,
                0, bitmap.getHeight(),
                bitmap.getWidth(), bitmap.getHeight()
        };

        matrix.getValues(dst);
        return dst;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDrawBtn:  //添加新图形
                Intent intent = new Intent(this, ThreeActivity.class);
                intent.putExtra("polygons", (Serializable) drawView.getAllModelList());
                intent.putExtra("scale", drawView.getmScale());

//                List<BaseView.ImageGroup> imageGroups = drawView.getmDecalImageGroupList();

//                ImageGroupModel imageGroupModel = ImageGroupModel.getInstance();
//                List<BaseView.ImageGroup> imageGroupList = imageGroupModel.getImageGroupList();
//                for (BaseView.ImageGroup imageGroup : imageGroupList) {
//                    try {
//                        float[] valuess = getValuess(imageGroup);
//                        imageGroup.setValues(valuess);
//                        imageGroup.matrix = null;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    byte[] bytes = Utils.getBytes(imageGroup.bitmap);
//                    imageGroup.setPic(bytes);
//                    imageGroup.bitmap = null;
//
//                }
//                intent.putExtra("decalImages", (Serializable) imageGroupList);

                startActivity(intent);
                this.finish();
                break;
            case R.id.saveDrawBtn: //保存图形（所有图形对象的集合）
                List<PoPoListModel> allModelList = drawView.getAllModelList();
                for (PoPoListModel poListModel : allModelList) {
                    for (Point point : poListModel.getList()) {
                        Log.e("FourActivity", "点坐标：" + point.getX() + "/" + point.getY());
                    }
                }

                List<ViewModel> list = new ArrayList<>();
                ViewModel viewModel = new ViewModel();
                viewModel.setPointList(allModelList);
                byte[] bytes = viewModel.getBytes(drawView.initBitmap());
                viewModel.setPic(bytes);
                list.add(viewModel);

                if (mSaveProgressDlg == null) {
                    initSaveProgressDlg();
                }
                mSaveProgressDlg.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bm = drawView.initBitmap();
                        String savedFile = saveImage(bm, 100);
                        if (savedFile != null) {
                            scanFile(FourActivity.this, savedFile);
                            mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                        }
                    }
                }).start();


                /**
                 * 方法一
                 */
                Intent intent1 = new Intent(this, Main2Activity.class);
                intent1.putExtra("list", (Serializable) list);
                setResult(RESULT_OK, intent1);

                /**
                 * 方法二
                 */
                ViewListModel viewListModel = ViewListModel.getInstance();
                viewListModel.addPointAndBitmapList(viewModel);


                finish();
                break;

            case R.id.bnt_ok_long:
                String longNum = etLineLong.getText().toString();
                if ((longNum == null || longNum.equals("")) && Double.valueOf(longNum) > 0) {
                    Toast.makeText(FourActivity.this, "请填写有效数值", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    drawView.setLongNum(Double.valueOf(longNum));
                    ScaleAnimation animation = new ScaleAnimation(1, 1, 1, 0);
                    animation.setDuration(500);
                    llLineSet.setAnimation(animation);
                    llLineSet.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.clear:
                etLineLong.setText("");
                break;
            case R.id.routerBtn:  //路由器
                routerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.router);
//                drawView.addRouter(true);
                drawView.addDecal(routerIcon);
                break;
            case R.id.amplifierBtn: //放大器
                amplifierIcon = BitmapFactory.decodeResource(getResources(), R.drawable.mplifier);
                drawView.addDecal(amplifierIcon);
                break;
            case R.id.testPointBtn: //测试点
                testPointIcon = BitmapFactory.decodeResource(getResources(), R.drawable.yuanquan);
                drawView.addDecal(testPointIcon);
                break;
            case R.id.homeplugBtn: //电力猫
                homeplugIcon = BitmapFactory.decodeResource(getResources(), R.drawable.homeplug);
                drawView.addDecal(homeplugIcon);
                break;
            case R.id.wifiBtn: //无线AP
                wifiIcon = BitmapFactory.decodeResource(getResources(), R.drawable.wifi);
                drawView.addDecal(wifiIcon);
                break;

            default:
                break;
        }
    }


    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                ImageGroupModel.getInstance().cleanGroupList();//保存后清空图标集合
                Toast.makeText(this, "画板已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    // 为了获取结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                //设置结果显示框的显示数值
                String editText = data.getStringExtra("editText");
                Toast.makeText(this, "editText:" + editText, Toast.LENGTH_SHORT).show();

                //将文字添加上去
                drawView.setAddText(true, editText);

            }
        }
    }


}
