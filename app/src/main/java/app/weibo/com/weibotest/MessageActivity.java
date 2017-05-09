package app.weibo.com.weibotest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;

import java.util.ArrayList;

/**
 * 微博的评论列表
 * Created by shuai on 2017/5/3.
 */

public class MessageActivity extends Activity {

    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 微博评论接口 */
    private CommentsAPI mCommentsAPI;
    private ListView listViewAll;
    private TextView aboutText;
    private TextView commentsCount;
    private Button getComments;
    private Button aboutMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        listViewAll= (ListView) findViewById(R.id.list_view_all);
        aboutText= (TextView) findViewById(R.id.about_text);
        commentsCount= (TextView) findViewById(R.id.comments_count);
        getComments= (Button) findViewById(R.id.comments_list);
        aboutMe= (Button) findViewById(R.id.about_me);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 获取微博评论信息接口
        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);

        getComments.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                //获取所有评论
                mCommentsAPI.toME(0L, 0L, 10, 1, CommentsAPI.AUTHOR_FILTER_ALL, CommentsAPI.SRC_FILTER_ALL, mListener);
                aboutText.setText(R.string.weibotest_app_message_comments_list);
            }
        });
        aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取@我的评论
                mCommentsAPI.mentions(0L,0L,10,1,CommentsAPI.AUTHOR_FILTER_ALL, CommentsAPI.SRC_FILTER_ALL, mListener);
                aboutText.setText(R.string.weibotest_app_message_about_me);
            }
        });
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener=new RequestListener() {
        @Override
        public void onComplete(String s) {
            CommentList comments = CommentList.parse(s);

            if (comments != null && comments.total_number > 0) {
            commentsCount.setText(comments.commentList.size()+"");
            ArrayList<String> list=new ArrayList<String>();
            for (int i=0;i<comments.commentList.size();i++){

                String str=comments.commentList.get(i).user.screen_name+":  "
                        +comments.commentList.get(i).text+"\n"
                        +comments.commentList.get(i).created_at;

                list.add(str);
            }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MessageActivity.this, android.R.layout.simple_list_item_1, list);
                listViewAll.setAdapter(adapter);
            }
        }
        @Override
        public void onWeiboException(WeiboException e) {

        }
    };

}