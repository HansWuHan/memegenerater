package hanswu.memegenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import hanswu.memegenerator.R;
import hanswu.memegenerator.TitleActivity;

public class DoodleActivity extends TitleActivity {
    private ImageView iv;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    Bitmap copyBitmap;

    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);

        setTitle("涂鸦");


        final Intent intent = getIntent();

        if(intent != null)
        {

            uri = intent.getParcelableExtra("uri");
            if (uri != null)
            {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Display display = getWindowManager().getDefaultDisplay();
            float dw = display.getWidth();
            float dh = display.getHeight();


                // 解析图片时需要使用到的参数都封装在这个对象里了
                BitmapFactory.Options options = new BitmapFactory.Options();
                // 不为像素申请内存，只获取图片宽高
                options.inJustDecodeBounds = true;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(uri), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 设置缩放比例
                int heightRatio = (int) Math.ceil(options.outHeight / dh);
                int widthRatio = (int) Math.ceil(options.outWidth / dw);
                if (heightRatio > 1 && widthRatio > 1) {
                    if (heightRatio > widthRatio) {
                        options.inSampleSize = heightRatio;
                    } else {
                        options.inSampleSize = widthRatio;
                    }
                }
                // 为像素申请内存
                options.inJustDecodeBounds = false;
                // 获取缩放后的图片
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(uri), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 创建缩放后的图片副本
                copyBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                        bitmap.getHeight(), bitmap.getConfig());



                this.iv = (ImageView) this.findViewById(R.id.iv);
            //iv.setBackground(new BitmapDrawable(bitmap));
        // 创建一张空白图片

        //baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            //baseBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), bitmap.getConfig());


        // 创建一张画布
        canvas = new Canvas(copyBitmap);
        // 创建画笔
        paint = new Paint();
        // 画笔颜色为黑色
        paint.setColor(Color.BLACK);
        // 宽度5个像素
        paint.setStrokeWidth(5);

        canvas.drawBitmap(copyBitmap, new Matrix(), paint);
        iv.setImageBitmap(copyBitmap);

        iv.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取手按下时的坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取手移动后的坐标
                        int stopX = (int) event.getX();
                        int stopY = (int) event.getY();
                        // 在开始和结束坐标间画一条线
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        // 实时更新开始坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        iv.setImageBitmap(copyBitmap);
                        break;
                }
                return true;
            }
        });
    }

        findViewById(R.id.color_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseColor();
            }
        });

        findViewById(R.id.paint_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseSize();
            }
        });

        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //绕过40k的传输限制，先存储，返回路径，回头读取，删除
                String uristring = MediaStore.Images.Media.insertImage(getContentResolver(), copyBitmap, "" , "");
                Uri originalUri = Uri.parse(uristring);
                Log.e("[Android]", "临时存到：" + originalUri);
                Intent data = new Intent();
                data.putExtra("uri", originalUri);
                setResult(Activity.RESULT_OK, data);
                finish();

            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    };

    private void ChooseColor(){
            new AlertDialog.Builder(this)
                .setTitle("选择颜色")
                .setSingleChoiceItems(new String[] { "红色", "绿色", "蓝色", "黑色" , "白色"}, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        paint.setColor(Color.RED);
                                        break;
                                    case 1:
                                        paint.setColor(Color.GREEN);
                                        break;
                                    case 2:
                                        paint.setColor(Color.BLUE);
                                        break;
                                    case 3:
                                        paint.setColor(Color.BLACK);
                                    case 4:
                                        paint.setColor(Color.WHITE);
                                    default:
                                        break;
                                }

                                dialog.dismiss();
                            }
                        }).show();
        }

        private void ChooseSize() {

            new AlertDialog.Builder(this)
                    .setTitle("选择画笔粗细")
                    .setSingleChoiceItems(new String[] { "细", "中", "粗" , "极粗" }, 0,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        case 0:
                                            paint.setStrokeWidth(5);
                                            break;
                                        case 1:
                                            paint.setStrokeWidth(10);
                                            break;
                                        case 2:
                                            paint.setStrokeWidth(15);
                                            break;
                                        case 3:
                                            paint.setStrokeWidth(20);
                                            break;
                                        default:
                                            break;
                                    }

                                    dialog.dismiss();
                                }
                            }).show();
        }

    }
