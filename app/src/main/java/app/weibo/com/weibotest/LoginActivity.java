package app.weibo.com.weibotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.widget.LoginButton;


/**
 * Created by shuai on 2017/5/2
 */

public class LoginActivity extends Activity {
    private Button mCurrentClickedButton;
    /**
     * 登陆认证对应的listener
     */
    private AuthListener mLoginListener = new AuthListener();
    private AuthInfo mAuthInfo;
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_login);
        // 创建授权认证信息
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        LoginButton mLoginBtnDefault = (LoginButton) findViewById(R.id.login_button_default);
        mLoginBtnDefault.setWeiboAuthInfo(mAuthInfo, mLoginListener);
        mLoginBtnDefault.setExternalOnClickListener(mButtonClickListener);

        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        token = AccessTokenKeeper.readAccessToken(this);

    }
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCurrentClickedButton != null) {
            if (mCurrentClickedButton instanceof LoginButton) {
                ((LoginButton)mCurrentClickedButton).onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                mCurrentClickedButton = (Button)v;
            }
        }
    };

    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            AccessTokenKeeper.writeAccessToken(LoginActivity.this, accessToken);
            Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    "取消授权", Toast.LENGTH_SHORT).show();
        }


    }
}



