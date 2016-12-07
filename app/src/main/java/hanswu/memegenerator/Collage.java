package hanswu.memegenerator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.Math.max;

public class Collage extends TitleActivity {
    Bitmap bitmap1,bitmap2;
    ImageView iv1;
    ImageView iv2;
    int b1 = 0, b2 =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);

        setTitle("合并");

        final TextView tv1 = (TextView)findViewById(R.id.textView1);
        final TextView tv2 = (TextView)findViewById(R.id.textView2);
        iv1 = (ImageView)findViewById(R.id.collageimageview1);
        iv2 = (ImageView)findViewById(R.id.collageimageview2);

        findViewById(R.id.chooseimage1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
                tv1.setText("图片一加载成功");
            }
        });

        findViewById(R.id.chooseimage2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
                tv2.setText("图片二加载成功");
            }
        });



        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b1==0 || b2 ==0) {
                    Toast.makeText(getApplicationContext(), "请选择图片",
                            Toast.LENGTH_SHORT).show();
                }else {

                    int width1 = bitmap1.getWidth();
                    int width2 = bitmap2.getWidth();
                    int height1 = bitmap1.getHeight();
                    int height2 = bitmap2.getHeight();
                    int x = 0;
                    if (width1 > width2) {
                        x = width1 - width2;
                    }
                    Bitmap bitmap = Bitmap.createBitmap(max(width1, width2), height1 + height2, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(bitmap1, new Matrix(), null);
                    canvas.drawBitmap(bitmap2, x, height1, null);
                    canvas.save(Canvas.ALL_SAVE_FLAG);
                    canvas.restore();

                    Intent intent = new Intent(Collage.this, ShareActivity.class);
                    //绕过40k的传输限制，先存储，返回路径，回头读取，删除
                    String uristring = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
                    Uri originalUri = Uri.parse(uristring);
                    Log.e("[Android]", "临时存到：" + originalUri);
                    b1 =0;
                    b2 =0;
                    intent.putExtra("uri", originalUri);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();
                Log.e("[Android]", "目录为：" + uri);
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    iv1.setImageBitmap(bitmap1);
                    b1 = 1;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == 2)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();
                Log.e("[Android]", "目录为：" + uri);
                try {
                    bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    iv2.setImageBitmap(bitmap2);
                    b2 = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
