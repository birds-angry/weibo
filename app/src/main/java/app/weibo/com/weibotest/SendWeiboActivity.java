package app.weibo.com.weibotest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;

/**
 * Created by shuai on 2017/5/3.
 */

public class SendWeiboActivity extends Activity  {
    public  static final int CHOOSE_PHOTO=1;

    private  Bitmap bitmap;
    private EditText weiboText;
    private ImageView picture;
    private Button choosePhoto;
    private Button sendText;
    private Button sendPicture;
    //当前 Token 信息
    private Oauth2AccessToken mAccessToken;
    //用于获取微博信息流等操作的API
    private StatusesAPI mStatusesAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_weibo);
        weiboText= (EditText) findViewById(R.id.weibo_text);
        picture= (ImageView) findViewById(R.id.picture);
        choosePhoto= (Button) findViewById(R.id.choose_photo);
        sendText= (Button) findViewById(R.id.send_text);
        sendPicture= (Button) findViewById(R.id.send_picture);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_PHOTO);
            }
        });

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String text=weiboText.getText().toString();
                mStatusesAPI.update(text, null, null, mListener);

            }
        });
        sendPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=weiboText.getText().toString();
                mStatusesAPI.upload(text, bitmap, null, null, mListener);
            }
        });

    }
    private RequestListener mListener=new RequestListener() {
        @Override
        public void onComplete(String s) {

            Toast.makeText(SendWeiboActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    };
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    handleImageOnKitKat(data);
                }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        String docId= DocumentsContract.getDocumentId(uri);
        if ("com.android.providers.media.documents".equals(uri.getAuthority())){
            String id = docId.split(":")[1];
            String selection= MediaStore.Images.Media._ID + "=" + id;
            imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
        }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
            Uri contentUri= ContentUris.withAppendedId(Uri.parse("content:downloads/public_downloads"),Long.valueOf(docId));
            imagePath=getImagePath(contentUri,null);
        }
        displayImage(imagePath);
    }


    private String getImagePath(Uri uri, String selection) {
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if (imagePath!=null) {
             bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }


    }




}