package hanswu.memegenerator;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.net.Uri;
import android.content.ContentResolver;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    //private ImageView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);

            }
        });

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        });

        findViewById(R.id.collage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Collage.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "未完成",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Bitmap cameraBitmap = (Bitmap)data.getExtras().get("data");

                String uristring = MediaStore.Images.Media.insertImage(getContentResolver(), cameraBitmap, "" , "");
                Uri originalUri = Uri.parse(uristring);
                Log.e("[Android]", "目录为：" + originalUri);
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                Intent intent = new Intent(MainActivity.this,edit.class);
                intent.putExtra("uri", originalUri);
                startActivity(intent);
            }
        }
        else if(requestCode == 2)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Uri originalUri = data.getData();
                Log.e("[Android]", "目录为：" + originalUri);
                // Bitmap cameraBitmap = null;
                // cameraBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), originalUri);
                Intent intenta = new Intent(MainActivity.this,edit.class);
                //intenta.putExtra("cameraBitmap", cameraBitmap);
                intenta.putExtra("uri", originalUri);
                startActivity(intenta);



            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    return;
                }
                break;

            default:
                break;
        }
    }

}
