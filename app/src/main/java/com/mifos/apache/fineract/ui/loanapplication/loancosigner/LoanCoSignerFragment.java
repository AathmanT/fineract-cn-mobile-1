package com.mifos.apache.fineract.ui.loanapplication.loancosigner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.mifos.apache.fineract.R;
import com.mifos.apache.fineract.data.models.loan.CreditWorthinessSnapshot;
import com.mifos.apache.fineract.ui.base.MifosBaseActivity;
import com.mifos.apache.fineract.ui.base.Toaster;
import com.mifos.apache.fineract.ui.loanapplication.BaseFragmentDebtIncome;
import com.mifos.apache.fineract.ui.loanapplication.OnNavigationBarListener;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Rajan Maurya
 *         On 19/07/17.
 */
public class LoanCoSignerFragment extends BaseFragmentDebtIncome implements Step,
        LoanCoSignerContract.View {

    @BindView(R.id.et_customer)
    AutoCompleteTextView etCustomer;

    @BindView(R.id.iv_search_customer)
    ImageView ivSearchCustomer;

    @Inject
    LoanCoSignerPresenter loanCoSignerPresenter;

    View rootView;

    String[] customers;

    private OnNavigationBarListener.LoanCoSignerData onNavigationBarListener;

    public static LoanCoSignerFragment newInstance() {
        LoanCoSignerFragment fragment = new LoanCoSignerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        loanCoSignerPresenter.attachView(this);
        rootView = view;
    }

    @OnClick(R.id.iv_search_customer)
    void searchCustomer() {
        if (TextUtils.isEmpty(etCustomer.getText().toString())) {
            Toaster.show(rootView, getString(R.string.customer_name_should_not_be_empty));
        } else {
            loanCoSignerPresenter.searchCustomer(etCustomer.getText().toString().trim());
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_loan_co_signer;
    }

    @Override
    public VerificationError verifyStep() {
        CreditWorthinessSnapshot creditWorthinessSnapshot = getCreditWorthinessSnapshot();
        if (!TextUtils.isEmpty(etCustomer.getText().toString().trim())) {
            if (loanCoSignerPresenter.findCustomer(etCustomer.getText().toString().trim(),
                    customers)) {
                creditWorthinessSnapshot.setForCustomer(etCustomer.getText().toString().trim());
            }
        }
        onNavigationBarListener.setCoSignerDebtIncome(creditWorthinessSnapshot);
        return null;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {
    }

    @Override
    public void showCustomers(List<String> customer) {
        customers = customer.toArray(new String[0]);
        ArrayAdapter<String> customerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, customers);
        etCustomer.setAdapter(customerAdapter);
        etCustomer.showDropDown();

    }

    @Override
    public void showProgressbar() {
        onNavigationBarListener.showProgressbar();
    }

    @Override
    public void hideProgressbar() {
        onNavigationBarListener.hideProgressbar();
    }

    @Override
    public void showError(String message) {
        Toaster.show(rootView, getString(R.string.error_loading_customers));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            onNavigationBarListener = (OnNavigationBarListener.LoanCoSignerData) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNavigationBarListener.LoanCoSignerData");
        }
    }

}
