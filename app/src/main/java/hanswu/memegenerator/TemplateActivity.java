package hanswu.memegenerator;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;

import hanswu.memegenerator.sticker.StickerImageView;

public class TemplateActivity extends TitleActivity {

    private Bitmap bitmap;
    private Uri uri= null;
    private FrameLayout canvasView;


    private StickerImageView template;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        final FrameLayout canvas = (FrameLayout) findViewById(R.id.canvasView);



        setTitle("模板");

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

            template = new StickerImageView(TemplateActivity.this);
            template.setImageDrawable(new BitmapDrawable(bitmap));
            canvas.addView(template);



        }

        findViewById(R.id.template1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set background of canvas
                canvasView = (FrameLayout)findViewById(R.id.canvasView);
                canvasView.setBackground(getResources().getDrawable(R.drawable.panda));


            }
        });

        findViewById(R.id.template2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvasView = (FrameLayout)findViewById(R.id.canvasView);
                canvasView.setBackground(getResources().getDrawable(R.drawable.cute));
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
                Log.e("?","?");
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

            template.setControlItemsHidden(Boolean.TRUE);

    }


}
