package cn.ucai.live.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.R;
import cn.ucai.live.data.NetDao;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.OnCompleteListener;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;

/**
 * Created by yu chen on 2017/3/6.
 */
public class ChangeActivity extends BaseActivity {
    @BindView(R.id.tv_change_balance)
    TextView mTvChangeBalance;
    @BindView(R.id.target_layout)
    LinearLayout mTargetLayout;
    View loadView;
    int change;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);
        loadView = LayoutInflater.from(ChangeActivity.this).inflate(R.layout.rp_loading, mTargetLayout, false);
        mTargetLayout.addView(loadView);
        setChange();
        initData();
    }

    private void initData() {
        NetDao.loadChange(ChangeActivity.this, EMClient.getInstance().getCurrentUser(), new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                boolean success = false;
                if (s!=null){
                    Result result = ResultUtils.getResultFromJson(s, Wallet.class);
                    if (result!=null&&result.isRetMsg()){
                        success = true;
                        Wallet wallet = (Wallet) result.getRetData();
                        PreferenceManager.getInstance().setCurrentChange(wallet.getBalance());
                        change = wallet.getBalance();
                        setChange();
                    }
                }
                if (!success){
                    PreferenceManager.getInstance().setCurrentChange(0);
                }
                loadView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                loadView.setVisibility(View.GONE);
                CommonUtils.showShortToast(error);
            }
        });
    }

    private void setChange() {
        change = PreferenceManager.getInstance().getCurrentChange();
        mTvChangeBalance.setText("￥ " + Float.valueOf(change));
    }
}
