package com.qiantu.youqian.module.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.qiantu.youqian.R;
import com.qiantu.youqian.api.app_action.BaseActionHelper;
import com.qiantu.youqian.base.BaseBarActivity;
import com.qiantu.youqian.module.login.bean.LoginBean;
import com.qiantu.youqian.module.login.presenter.LoginPresenter;
import com.qiantu.youqian.module.login.presenter.LoginViewer;
import com.qiantu.youqian.third_service.sysytem.GeneralUtils;

import qianli.base.framework.mvp.PresenterLifeCycle;
import yuntu.common.simplify.loop.Looper;
import yuntu.common.util_lib.ToastUtil;

import static com.qiantu.youqian.config.Constant.CODE_LENGTH;
import static com.qiantu.youqian.config.Constant.MOBILE_LENGTH;


/**
 * @author zhangxianpei
 */
public class LoginActivity extends BaseBarActivity implements LoginViewer, View.OnClickListener {

    /**
     * 标志是否存在，存在时候重新启动，主要用于单点登录
     */
    public static boolean pFlag = false;

    @PresenterLifeCycle
    LoginPresenter presenter = new LoginPresenter(this);
    private EditText mEditCode;
    private EditText mEditMobile;
    private Button mBtnLogin;
    private TextView mUserRegisterAgreement;
    private TextView mSendVerCode;

    /**
     * 跳转客服之类的
     * 不能在登录情况下调用
     */
    public static Intent callRedirectOtherActionIntent(Context context, String targetOther, @Nullable Bundle bundle) {
        return LoginRedirectHelper.setRedirectData(context, LoginActivity.class, bundle, "", targetOther);
    }

    @Override
    protected void setView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_view);
        setTitle("");
        setIcon(R.drawable.ic_black_close);
        setToolbarBackgroundColor(Color.WHITE);
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();

        mEditCode = bindView(R.id.edit_code);
        mEditMobile = bindView(R.id.edit_mobile);
        mBtnLogin = bindView(R.id.btn_login, this);
        mUserRegisterAgreement = bindView(R.id.user_register_agreement);
        mSendVerCode = bindView(R.id.send_ver_code, this);
        initEvent();

    }

    private void initEvent() {
        String registerAgreement = getString(R.string.all_register_agreement);
        String termsConditionsTwo = getString(R.string.privacy_policy);
        int twoStart = registerAgreement.indexOf(termsConditionsTwo);
        int twoEnd = twoStart + termsConditionsTwo.length();
        SpannableString spannable = new SpannableString(registerAgreement);
        spannable.setSpan(new ClickableSpan() {
                              @Override
                              public void onClick(@NonNull View view) {
                                  if (GeneralUtils.isFastClick()) {
                                    BaseActionHelper.with(getActivity()).handleAction("https://loanmarket2.oss-cn-hongkong.aliyuncs.com/resource/agreement.html");
                                  }
                              }

                              @Override
                              public void updateDrawState(TextPaint ds) {
                                  ds.setColor(getResources().getColor(R.color.color_FF5D00));
                                  ds.setUnderlineText(true);
                              }
                          },
                twoStart, twoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mUserRegisterAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        mUserRegisterAgreement.setText(spannable);


        mEditMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= MOBILE_LENGTH) {
                    mSendVerCode.setEnabled(true);
                } else {
                    mSendVerCode.setEnabled(false);
                }
            }
        });
        mEditCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditMobile.length() > 0 && s.length() >= CODE_LENGTH) {
                    mBtnLogin.setEnabled(true);
                } else {
                    mBtnLogin.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void getVerifycodeSendSuccess(String msg) {
        ToastUtil.showToast(msg);
        mLooper.startLoop();
        mSendVerCode.setTextColor(getResources().getColor(R.color.color_BFBEC1));
        mSendVerCode.setBackgroundResource(R.drawable.shape_get_otp_enter);
    }

    @Override
    public void getLoginVerifycodeSuccess(LoginBean loginBean) {

    }

    @Override
    public void getLoginVerifycodeFail(String errorMsg) {
        ToastUtil.showToast(errorMsg);
    }

    @Override
    public void setResult() {
        setResult(RESULT_OK);
    }

    @Override
    public Bundle getLoginExtraBundle() {
        return LoginRedirectHelper.getLoginExtraBundle(getActivity());
    }

    @Override
    public String getRedirectActivityClassName() {
        return LoginRedirectHelper.getRedirectActivityClassName(getActivity());
    }

    @Override
    public String getRedirectOtherAction() {
        return LoginRedirectHelper.getRedirectOtherAction(getActivity());
    }

    @Override
    public void onClick(View view) {
        String mobile = mEditMobile.getText().toString();
        switch (view.getId()) {
            case R.id.send_ver_code:
                if (mobile.indexOf("0") == 0 && mobile.length() >= 10) {
                    presenter.getVerifycodeSend(mobile);
                } else {
                    ToastUtil.showToast(getString(R.string.correct_phone));
                }

                break;
            case R.id.btn_login:
                if (mobile.indexOf("0") == 0 && mobile.length() >= 10) {
                    presenter.getLoginVerifycode(mEditMobile.getText().toString(), mEditCode.getText().toString());
                } else {
                    ToastUtil.showToast(getString(R.string.correct_phone));
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Looper<Integer> mLooper = new Looper<Integer>() {

        @Override
        protected void onStart() {
            setTransferData(60);
        }

        @Override
        public Integer handleData(Integer times) {
            return times - 1;
        }

        @Override
        public int getInterval() {
            return 1000;
        }

        @Override
        protected void onNexted(Integer integer) {
            if (getActivity() == null || getActivity().isFinishing()) {
                stopLooper();
                return;
            }
            if (integer > 0) {
                bindText(R.id.send_ver_code, integer + "s");
                bindEnable(R.id.send_ver_code, false);
                nextLoop();
            } else {
                stopLooper();
            }
        }

        @Override
        protected void onCompleted() {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            bindText(R.id.send_ver_code, getString(R.string.all_get_verification_code));
            bindEnable(R.id.send_ver_code, true);
            mSendVerCode.setBackgroundResource(R.drawable.shape_get_otp);
            mSendVerCode.setTextColor(getResources().getColor(R.color.color_FF5D00));
        }
    };
}
