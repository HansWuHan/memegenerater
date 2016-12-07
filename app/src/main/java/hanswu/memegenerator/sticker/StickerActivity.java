package hanswu.memegenerator.sticker;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.io.IOException;

import hanswu.memegenerator.R;
import hanswu.memegenerator.TitleActivity;

public class StickerActivity extends TitleActivity {

    private Bitmap bitmap;
    private Uri uri= null;
    private FrameLayout canvasView;
    private int FLAG_TEXT_COLOR = 0;
    private  int[] savemode= new int[17];


    private StickerImageView[] stickerimage = new StickerImageView[17];
    private StickerTextView[] stickertext = new StickerTextView[17];


    public static int sticker_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        final FrameLayout canvas = (FrameLayout) findViewById(R.id.canvasView);



        setTitle("贴纸");

        Intent intent = getIntent();
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

            //set background of canvas
            canvasView = (FrameLayout)findViewById(R.id.canvasView);
            Drawable drawable =new BitmapDrawable(bitmap);
            canvasView.setBackground(drawable);
        }

        findViewById(R.id.sticker1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stickerimage[sticker_num] = new StickerImageView(StickerActivity.this);
                stickerimage[sticker_num].setImageDrawable(getResources().getDrawable(R.drawable.no));
                canvas.addView(stickerimage[sticker_num]);
                savemode[sticker_num] = 1;
                sticker_num++;

            }
        });

        findViewById(R.id.sticker2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerimage[sticker_num] = new StickerImageView(StickerActivity.this);
                stickerimage[sticker_num].setImageDrawable(getResources().getDrawable(R.drawable.horse));
                canvas.addView(stickerimage[sticker_num]);
                savemode[sticker_num] = 1;
                sticker_num++;
            }
        });

        findViewById(R.id.sticker3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerimage[sticker_num] = new StickerImageView(StickerActivity.this);
                stickerimage[sticker_num].setImageDrawable(getResources().getDrawable(R.drawable.smile));
                canvas.addView(stickerimage[sticker_num]);
                savemode[sticker_num] = 1;
                sticker_num++;
            }
        });

        findViewById(R.id.sticker4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickertext[sticker_num] = new StickerTextView(StickerActivity.this);
                settextsticker();//居然弹出框不等我就先执行后面了！只好把++放在弹出框里
                canvas.addView(stickertext[sticker_num]);

            }
        });

        findViewById(R.id.sticker5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerimage[sticker_num] = new StickerImageView(StickerActivity.this);
                stickerimage[sticker_num].setImageDrawable(getResources().getDrawable(R.drawable.ribbon));
                canvas.addView(stickerimage[sticker_num]);
                savemode[sticker_num] = 1;
                sticker_num++;
            }
        });




        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlinvisible();
                canvasView.setDrawingCacheEnabled(true);
                canvasView.buildDrawingCache();  //启用DrawingCache并创建位图
                bitmap = Bitmap.createBitmap(canvasView.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
                canvasView.setDrawingCacheEnabled(false);

                //绕过40k的传输限制，先存储，返回路径，回头读取，删除
                String uristring = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "" , "");
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * Drawable 转 bitmap
     * @param drawable
     * @return
     */
    public static Bitmap Drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }else{
            return null ;
        }
    }

    protected void controlinvisible(){
        while (sticker_num > 0){
            sticker_num--;
            Log.e("?",Integer.toString(sticker_num));
            if(savemode[sticker_num] == 1) {
                stickerimage[sticker_num].setControlItemsHidden(Boolean.TRUE);
            }else{
                stickertext[sticker_num].setControlItemsHidden(Boolean.TRUE);
            }
        }
    }

    protected  void settextsticker(){
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("文字贴图")
                .setSingleChoiceItems(new String[] { "红色", "黑色" , "白色"}, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                stickertext[sticker_num].setColor(Color.RED); FLAG_TEXT_COLOR = 1; break;
                            case 1:
                                stickertext[sticker_num].setColor(Color.BLACK); FLAG_TEXT_COLOR = 1; break;
                            case 2:
                                stickertext[sticker_num].setColor(Color.WHITE); FLAG_TEXT_COLOR = 1; break;
                        }
                    }
                } )
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            String input = et.getText().toString();
                            if (input.equals("")) {

                            }
                            else {
                                stickertext[sticker_num].setText(input);
                            }

                        savemode[sticker_num] = 0;
                        sticker_num++;

                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    public static void sticker_num_decline(){
        sticker_num--;
    }
}
