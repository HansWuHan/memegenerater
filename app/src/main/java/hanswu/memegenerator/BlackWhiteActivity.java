package hanswu.memegenerator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.IOException;

public class BlackWhiteActivity extends TitleActivity {

    private ImageView imageview;
    private Bitmap bitmap;
    private Uri uri;
    private SeekBar seekbar;
    private Bitmap bmpGray = null, bitmapresult;
    public static Boolean BWSTATE = Boolean.FALSE;
    private int FLAG_GREY_BW = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_white);

        setTitle("黑白");


        final Intent intent = getIntent();
        imageview = (ImageView) findViewById(R.id.ImageBlackWhite);


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

            imageview.setImageBitmap(bitmap);

            initialSeekBar();
            initialbutton();

            findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BWSTATE = Boolean.TRUE;
                    Intent data = new Intent();
                    if(FLAG_GREY_BW == 0) {
                        bitmapresult = bitmap;
                    }
                    //绕过40k的传输限制，先存储，返回路径，回头读取，删除
                    String uristring = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapresult, "" , "");
                    Uri originalUri = Uri.parse(uristring);
                    Log.e("[Android]", "临时存到：" + originalUri);
                    data = new Intent();
                    data.putExtra("uri", originalUri);
                    setResult(Activity.RESULT_OK, data);
                    finish();


                }
            });

            findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BWSTATE = Boolean.FALSE;
                    finish();
                }
            });
        }
    }


    private void initialSeekBar(){
        seekbar =(SeekBar) findViewById(R.id.SeekBarBlackWhite);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(bitmap==null){

                }else{
                    int tmp=(int)(progress*2.55);
                    Bitmap bitmapTmp=bitmap;
                    bitmapTmp = convertToBMW (bitmapTmp, tmp);
                    // 显得到bitmap图片
                    imageview.setImageBitmap(bitmapTmp);
                    FLAG_GREY_BW = 1;
                    bitmapresult = bitmapTmp;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void initialbutton(){
        findViewById(R.id.ButtonBlackWhite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 得到图片的长和宽

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                // 创建目标灰度图像

                bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                // 创建画布
                Canvas c = new Canvas(bmpGray);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);
                c.drawBitmap(bitmap, 0, 0, paint);
                imageview.setImageBitmap(bmpGray);
                bitmap = bmpGray;
                FLAG_GREY_BW = 0;
            }
        });
    }




    public static Bitmap convertToBMW(Bitmap bmp, int tmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
        // 设定二值化的域值，默认值为100
        //tmp = 180;
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                // 分离三原色
                alpha = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                if (red > tmp) {
                    red = 255;
                }
                if (blue > tmp) {
                    blue = 255;
                }
                if (green > tmp) {
                    green = 255;
                }
                pixels[width * i + j] = alpha << 24 | red << 16 | green << 8
                        | blue;
                if (pixels[width * i + j] == -1) {
                    pixels[width * i + j] = -1;
                } else {
                    pixels[width * i + j] = -16777216;
                }
            }
        }
        // 新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        return newBmp;
    }
}
