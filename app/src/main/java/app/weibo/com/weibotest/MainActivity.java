package app.weibo.com.weibotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by shuai on 2017/5/3.
 */

public class MainActivity extends Activity {

     Oauth2AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        //获取用户信息
        this.findViewById(R.id.user).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
            }
        });

        //发送微博
        this.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SendWeiboActivity.class));
            }
        });
        //查看消息
        this.findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MessageActivity.class));
            }
        });

            AccessTokenKeeper.readAccessToken(MainActivity.this);
            if (accessToken == null ) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
        }
    }


}
