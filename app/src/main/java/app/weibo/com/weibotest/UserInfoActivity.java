package app.weibo.com.weibotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取用户信息
 * 获取用户的粉丝数、关注数、微博数
 * Created by shuai on 2017/5/3.
 */

public class UserInfoActivity extends Activity implements View.OnClickListener {

    private Button logout;
    private TextView userInfo;
    private Button showUserInfo;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用户信息接口 */
    private UsersAPI usersAPI;
    /**注销操作回调*/
    LogOutRequestListener mLogoutRequestListener=new LogOutRequestListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_info_show);
        showUserInfo = (Button) findViewById(R.id.show_user_info);
        userInfo = (TextView) findViewById(R.id.user_info);
        logout= (Button) findViewById(R.id.logout);
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);

        // 获取用户信息接口
        usersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);

        showUserInfo.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_user_info:
                long uid = Long.parseLong(mAccessToken.getUid());
                usersAPI.show(uid, mListener);
                break;
            case R.id.logout:
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    new LogoutAPI(UserInfoActivity.this, Constants.APP_KEY, mAccessToken).logout(mLogoutRequestListener);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);

                userInfo.setText("\n用户名：" + user.screen_name
                        + "\n微博: " + user.statuses_count
                        + "\n关注: " + user.friends_count
                        + "\n粉丝: " + user.followers_count);
            }
        }
            @Override
            public void onWeiboException (WeiboException e){
            }
        };

    /**
     * 登出按钮的监听器，接收登出处理结果。
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(UserInfoActivity.this);
                        Toast.makeText(UserInfoActivity.this,R.string.weibotest_app_has_been_canceled,Toast.LENGTH_SHORT).show();
                        mAccessToken = null;
                        Intent intent=new Intent(UserInfoActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(UserInfoActivity.this,R.string.weibotest_app_logout_failed,Toast.LENGTH_SHORT).show();
        }
    }

}
