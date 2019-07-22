package com.qiantu.youqian.module.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.qiantu.youqian.R;
import com.qiantu.youqian.api.bean.response.ProductList;
import com.qiantu.youqian.base.BaseBarActivity;
import com.qiantu.youqian.base.utils.BaseSharedDataUtil;
import com.qiantu.youqian.module.profile.adapter.ProfileListAdapter;
import com.qiantu.youqian.module.profile.adapter.ProfileProductAdapter;
import com.qiantu.youqian.module.profile.bean.ProfileActionList;
import com.qiantu.youqian.module.profile.presenter.ProfileDataPresenter;
import com.qiantu.youqian.module.profile.presenter.ProfileDataViewer;

import qianli.base.framework.mvp.PresenterLifeCycle;

/**
 * @author zhangxianpei
 */
public class ProfileDataActivity extends BaseBarActivity implements ProfileDataViewer {

    @PresenterLifeCycle
    ProfileDataPresenter presenter = new ProfileDataPresenter(this);
    private RecyclerView mCertificationDataRecycle;
    private ProfileListAdapter mAdapter;
    private ProfileProductAdapter mProductAdapter;
    private RecyclerView mPreferredListRecycler;

    @Override
    protected void setView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile_data_view);
        setTitle(this.getResources().getString(R.string.profile));

        mCertificationDataRecycle = bindView(R.id.certification_data_recycle);
        mCertificationDataRecycle.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProfileListAdapter(this);
        mCertificationDataRecycle.setAdapter(mAdapter);


    }

    private void setMatchedLoans() {
        bindView(R.id.cl_product_module, true);
        mPreferredListRecycler = (RecyclerView) findViewById(R.id.preferred_list_recycler);
        mProductAdapter = new ProfileProductAdapter(getActivity());
        mPreferredListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPreferredListRecycler.setAdapter(mProductAdapter);
        bindView(R.id.preferred_more, false);
        bindText(R.id.preferred_list, this.getResources().getString(R.string.matched_loans));
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void getProfileActionListSuccess(ProfileActionList actionList) {
        if (!TextUtils.isEmpty(actionList.getTips())) {
            bindText(R.id.profile_tip, actionList.getTips());
        }
        mAdapter.setCollection(actionList.getActionGroup().get(0).getActions());
        if (actionList.isCertifyComplete()){
            presenter.getProfileProductList();
        }
    }

    @Override
    public void getProfileActionListFail(String errorMsg) {

    }

    @Override
    public void getProfileProductListSuccess(ProductList productList) {
        if (productList != null && productList.getProductList().size() > 0) {
            //当全部资料认证完成后显示
            setMatchedLoans();
            mProductAdapter.setCollection(productList.getProductList());
        }
    }

    @Override
    public void getProfileProductListFail() {

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getProfileActionList();
    }
}
