package app.weibo.com.weibotest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by shuai on 2017/5/3.
 */

public class UserInfoActivity extends Activity implements View.OnClickListener {
    private TextView userInfo;
    private Button showUserInfo;
    private Oauth2AccessToken mAccessToken;
    private UsersAPI usersAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_show);
        showUserInfo = (Button) findViewById(R.id.show_user_info);
        userInfo = (TextView) findViewById(R.id.user_info);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);

        // 获取用户信息接口
        usersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);

        showUserInfo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_user_info:
                long uid = Long.parseLong(mAccessToken.getUid());
                usersAPI.show(uid, mListener);
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

            User user = User.parse(response);

            userInfo.setText("用户名："+user.screen_name+"\n微博：" + user.statuses_count + "\n关注：" + user.friends_count + "\n粉丝:" + user.followers_count);
        }

            @Override
            public void onWeiboException (WeiboException e){
            }
        };
}
