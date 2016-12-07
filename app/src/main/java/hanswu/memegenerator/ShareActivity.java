package hanswu.memegenerator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import hanswu.memegenerator.sticker.StickerImageView;

import static android.R.attr.bitmap;

public class ShareActivity extends TitleActivity {

    Uri uri;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setTitle("分享");
        ImageView iv = (ImageView)findViewById(R.id.imageViewShare);
        Intent intent = getIntent();
        if(intent != null)
        {
            uri = intent.getParcelableExtra("uri");
            if (uri != null)
            {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    iv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShareActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(ShareActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "未完成",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "未完成",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "未完成",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
