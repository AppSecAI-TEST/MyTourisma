package com.ftl.tourisma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.utils.Constants;

/**
 * Created by Vinay on 6/7/2017.
 */

public class TicketPersonalDetails extends Fragment {

    View view;
    MainActivity mainActivity;
    ImageView img_close;
    EditText first_name_edtxt, last_name_edtxt, email_edtxt, mobile_edtxt, addr_edtxt;
    TextView continue_btn, txtTitle;
    String mDate;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ticket_personal_details, container, false);
        dcl_layout_variable(view);
        onClickListners();

        Bundle mIntent = getArguments();
        mDate = mIntent.getString("DATE");

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        return view;
    }

    public void dcl_layout_variable(View view) {
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        continue_btn = (TextView) view.findViewById(R.id.continue_btn);
        first_name_edtxt = (EditText) view.findViewById(R.id.first_name_edtxt);
        last_name_edtxt = (EditText) view.findViewById(R.id.last_name_edtxt);
        email_edtxt = (EditText) view.findViewById(R.id.email_edtxt);
        mobile_edtxt = (EditText) view.findViewById(R.id.mobile_edtxt);
        addr_edtxt = (EditText) view.findViewById(R.id.addr_edtxt);
        img_close = (ImageView) view.findViewById(R.id.img_close);
    }

    public void onClickListners() {

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (first_name_edtxt.getText().toString().length() == 0) {
//                    SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NAME")));
//                } else if (email_edtxt.getText().toString().length() == 0) {
//                    SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "EMAIL")));
//                } else if (!Constants.isValidEmail(email_edtxt.getText().toString())) {
//                    SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "VALIDEMAIL")));
//                } else {
                    Fragment fragment = new TicketEventOptions();
                    Bundle bundle = new Bundle();
                    bundle.putString("DATE", mDate);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fram1, fragment);
                    fragmentTransaction.addToBackStack(TicketEventOptions.class.getSimpleName());
                    fragmentTransaction.commit();
//                }
            }
        });
    }
}
