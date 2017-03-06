package cn.ucai.live.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.R;

/**
 * Created by yu chen on 2017/3/6.
 */
public class ChangeActivity extends BaseActivity {
    @BindView(R.id.tv_change_balance)
    TextView mTvChangeBalance;
    @BindView(R.id.target_layout)
    LinearLayout mTargetLayout;
    View loadView;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);
        loadView = LayoutInflater.from(ChangeActivity.this).inflate(R.layout.rp_loading,null);
        mTargetLayout.addView(loadView);
    }
}
