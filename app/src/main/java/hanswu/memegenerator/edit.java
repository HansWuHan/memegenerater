package hanswu.memegenerator;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import hanswu.memegenerator.sticker.StickerActivity;

public class edit extends TitleActivity{
    private ImageView imageview;
    private Bitmap bitmap;
    private Uri uri= null, passeduri = null;
    int CONVERT_BW = 5;
    int STICKER = 3;
    int TEMPLATE = 4;
    int DOODLE = 2;
    int FLAG_URI_CHANGED = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setTitle("编辑");

        imageview = (ImageView) findViewById(R.id.imagedit);

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
            imageview.setImageBitmap(bitmap);
        }

        findViewById(R.id.cut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop();
            }
        });

        findViewById(R.id.bw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startconvertBW();
            }
        });

        findViewById(R.id.sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startaddsticker();
            }
        });

        findViewById(R.id.template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttemplate();
            }
        });

        findViewById(R.id.doodle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "未完成",
                        Toast.LENGTH_SHORT).show();
                //startdoodle();
            }
        });

        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit.this,ShareActivity.class);
                String uristring = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "" , "");
                Uri originalUri = Uri.parse(uristring);
                Log.e("[Android]", "临时存到：" + originalUri);

                FLAG_URI_CHANGED = 0;
                intent.putExtra("uri", originalUri);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLAG_URI_CHANGED = 0;
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FLAG_URI_CHANGED == 1){//因为图片会保存在photo里，所以要保证每时只有一个处理中的图片保存在里面，否则处理程序全部完成，就会塞满半成品图片
            deleteimagefromuri(passeduri);
            passeduri = uri;
        }else {
            FLAG_URI_CHANGED = 1;
            passeduri = uri;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            uri = UCrop.getOutput(data);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageview.setImageBitmap(bitmap);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

        if (resultCode == RESULT_OK && requestCode == CONVERT_BW){
            if(BlackWhiteActivity.BWSTATE == Boolean.TRUE){
                uri = (Uri)data.getExtras().get("uri");
                if (FLAG_URI_CHANGED == 1){//因为图片会保存在photo里，所以要保证每时只有一个处理中的图片保存在里面，否则处理程序全部完成，就会塞满半成品图片
                    deleteimagefromuri(passeduri);
                    passeduri = uri;
                }else {
                    FLAG_URI_CHANGED = 1;
                    passeduri = uri;
                }

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imageview.setImageBitmap(bitmap);
                BlackWhiteActivity.BWSTATE = Boolean.FALSE;
            }
        }

        if (resultCode == RESULT_OK && requestCode == STICKER){
            uri = (Uri)data.getExtras().get("uri");
            if (FLAG_URI_CHANGED == 1){//因为图片会保存在photo里，所以要保证每时只有一个处理中的图片保存在里面，否则处理程序全部完成，就会塞满半成品图片
                deleteimagefromuri(passeduri);
                passeduri = uri;
            }else {
                FLAG_URI_CHANGED = 1;
                passeduri = uri;
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageview.setImageBitmap(bitmap);
        }

        if (resultCode == RESULT_OK && requestCode == TEMPLATE){

            uri = (Uri)data.getExtras().get("uri");
            if (FLAG_URI_CHANGED == 1){//因为图片会保存在photo里，所以要保证每时只有一个处理中的图片保存在里面，否则处理程序全部完成，就会塞满半成品图片
                deleteimagefromuri(passeduri);
                passeduri = uri;
            }else {
                FLAG_URI_CHANGED = 1;
                passeduri = uri;
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageview.setImageBitmap(bitmap);
        }

        if (resultCode == RESULT_OK && requestCode == DOODLE){

            uri = (Uri)data.getExtras().get("uri");
            if (FLAG_URI_CHANGED == 1){//因为图片会保存在photo里，所以要保证每时只有一个处理中的图片保存在里面，否则处理程序全部完成，就会塞满半成品图片
                deleteimagefromuri(passeduri);
                passeduri = uri;
            }else {
                FLAG_URI_CHANGED = 1;
                passeduri = uri;
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageview.setImageBitmap(bitmap);
        }
    }

    private void startCrop() {
        Uri sourceUri = uri;
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "SampleCropImage.jpeg"));
        UCrop.of(sourceUri, destinationUri).withMaxResultSize(300, 300).start(this);
    }

    private void startconvertBW() {
        Uri sourceUri = uri;
        Intent intent = new Intent(edit.this, BlackWhiteActivity.class);
        intent.putExtra("uri", sourceUri);
        startActivityForResult(intent,CONVERT_BW);
    }

    private void startaddsticker(){
        Uri sourceUri = uri;
        Intent intent = new Intent(edit.this, StickerActivity.class);
        intent.putExtra("uri", sourceUri);
        startActivityForResult(intent,STICKER);
    }

    private void starttemplate(){
        Uri sourceUri = uri;
        Intent intent = new Intent(edit.this, TemplateActivity.class);
        intent.putExtra("uri", sourceUri);
        startActivityForResult(intent,TEMPLATE);
    }

    private void startdoodle(){
        Uri sourceUri = uri;
        Intent intent = new Intent(edit.this, DoodleActivity.class);
        intent.putExtra("uri", sourceUri);
        //startActivityForResult(intent,DOODLE);
    }

    public void deleteimagefromuri(Uri uri){
        String path = uritopath(uri);
        Log.e("path"," = "+path);
        if (!TextUtils.isEmpty(path)) {
            Log.e("delete","successful");
            File file = new File(path);
            if (file.exists())
                file.delete();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    private  String uritopath(Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String path = actualimagecursor.getString(actual_image_column_index);
        return path;
    }



}
