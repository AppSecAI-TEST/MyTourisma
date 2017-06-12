package com.ftl.tourisma;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.models.TicketList;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;

/**
 * Created by Vinay on 6/7/2017.
 */

public class TicketEventOptions extends Fragment {

    Integer cart_count_adlt = 0;
    Integer cart_count_senr = 0;
    Integer cart_count_chld = 0;
    Integer cart_count_infant = 0;
    ArrayList<TicketList> ticketLists = new ArrayList<>();
    View view;
    MainActivity mainActivity;
    ImageView img_close, add_qyt_senr, sub_qyt_senr, add_qyt_adlt, sub_qyt_adlt, add_qyt_chld, sub_qyt_chld, add_qyt_infant, sub_qyt_infant;
    TextView continue_btn, apply_promo_btn, total_txt, final_total_amt_txt, txtTitle, select_options_txt, tickets_txt, promo_txt_main, promo_txt;
    TextView senior_txt, qty_txt_senr, amt_txt_senr;
    TextView adult_txt, qty_txt_adlt, amt_txt_adlt;
    TextView child_txt, qty_txt_chld, amt_txt_chld;
    TextView infant_txt, qty_txt_infant, amt_txt_infant;
    EditText promo_edtxt;
    RelativeLayout calender_layout, event_options_layout, promo_code_layout;
    RecyclerView timings_recycler;
    boolean isVisible = true;
    TextView dateText;
    String mDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ticket_event_options, container, false);
        dcl_layout_variable(view);

        cart_count_adlt = 0;
        cart_count_senr = 0;
        cart_count_chld = 0;
        cart_count_infant = 0;

        onClickListners();

        Bundle mIntent = getArguments();
        mDate = mIntent.getString("DATE");
        dateText.setText(mDate);

        return view;
    }

    public void dcl_layout_variable(View view) {
        img_close = (ImageView) view.findViewById(R.id.img_close);
        add_qyt_senr = (ImageView) view.findViewById(R.id.add_qyt_senr);
        sub_qyt_senr = (ImageView) view.findViewById(R.id.sub_qyt_senr);
        add_qyt_adlt = (ImageView) view.findViewById(R.id.add_qyt_adlt);
        sub_qyt_adlt = (ImageView) view.findViewById(R.id.sub_qyt_adlt);
        add_qyt_chld = (ImageView) view.findViewById(R.id.add_qyt_chld);
        sub_qyt_chld = (ImageView) view.findViewById(R.id.sub_qyt_chld);
        add_qyt_infant = (ImageView) view.findViewById(R.id.add_qyt_infant);
        sub_qyt_infant = (ImageView) view.findViewById(R.id.sub_qyt_infant);
        apply_promo_btn = (TextView) view.findViewById(R.id.apply_promo_btn);
        total_txt = (TextView) view.findViewById(R.id.total_txt);
        final_total_amt_txt = (TextView) view.findViewById(R.id.final_total_amt_txt);
        promo_txt_main = (TextView) view.findViewById(R.id.promo_txt_main);
        promo_txt = (TextView) view.findViewById(R.id.promo_txt);
        senior_txt = (TextView) view.findViewById(R.id.senior_txt);
        qty_txt_senr = (TextView) view.findViewById(R.id.qty_txt_senr);
        amt_txt_senr = (TextView) view.findViewById(R.id.amt_txt_senr);
        adult_txt = (TextView) view.findViewById(R.id.adult_txt);
        qty_txt_adlt = (TextView) view.findViewById(R.id.qty_txt_adlt);
        amt_txt_adlt = (TextView) view.findViewById(R.id.amt_txt_adlt);
        child_txt = (TextView) view.findViewById(R.id.child_txt);
        qty_txt_chld = (TextView) view.findViewById(R.id.qty_txt_chld);
        amt_txt_chld = (TextView) view.findViewById(R.id.amt_txt_chld);
        infant_txt = (TextView) view.findViewById(R.id.infant_txt);
        qty_txt_infant = (TextView) view.findViewById(R.id.qty_txt_infant);
        amt_txt_infant = (TextView) view.findViewById(R.id.amt_txt_infant);
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        continue_btn = (TextView) view.findViewById(R.id.continue_btn);
        select_options_txt = (TextView) view.findViewById(R.id.select_options_txt);
        tickets_txt = (TextView) view.findViewById(R.id.tickets_txt);
        calender_layout = (RelativeLayout) view.findViewById(R.id.relativeLayout1);
        event_options_layout = (RelativeLayout) view.findViewById(R.id.relativeLayout2);
        promo_code_layout = (RelativeLayout) view.findViewById(R.id.relativeLayout3);
        promo_edtxt = (EditText) view.findViewById(R.id.promo_edtxt);
        timings_recycler = (RecyclerView) view.findViewById(R.id.timings_recycler);
        dateText = (TextView) view.findViewById(R.id.date_txt);
    }

    public void onClickListners() {

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

        event_options_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new TicketEvent();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment);
                fragmentTransaction.addToBackStack(TicketEvent.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        promo_txt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible) {
                    promo_code_layout.setVisibility(View.VISIBLE);
                    isVisible = false;
                } else {
                    promo_code_layout.setVisibility(View.GONE);
                    isVisible = true;
                }
            }
        });

        apply_promo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promo_edtxt.getText().toString().length() == 0) {
                    SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text("Please enter promo code"));
                } else {

                }
            }
        });

        add_qyt_senr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart_count_senr++;
                if (cart_count_senr > 0) {
                    qty_txt_senr.setText("" + cart_count_senr);
                    sub_qyt_senr.setEnabled(true);
                } else {
                    sub_qyt_senr.setEnabled(false);
                }
            }
        });

        sub_qyt_senr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart_count_senr--;
                if (cart_count_senr < 0) {
                    qty_txt_senr.setText("" + 0);
                    sub_qyt_senr.setEnabled(false);
                } else {
                    add_qyt_senr.setEnabled(true);
                    qty_txt_senr.setText("" + cart_count_senr);
                }
            }
        });

        add_qyt_adlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart_count_adlt++;
                if (cart_count_adlt > 0) {
                    qty_txt_adlt.setText("" + cart_count_adlt);
                    sub_qyt_adlt.setEnabled(true);
                } else {
                    sub_qyt_adlt.setEnabled(false);
                }
            }
        });

        sub_qyt_adlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart_count_adlt--;
                if (cart_count_adlt < 0) {
                    qty_txt_adlt.setText("" + 0);
                    sub_qyt_adlt.setEnabled(false);
                } else {
                    add_qyt_adlt.setEnabled(true);
                    qty_txt_adlt.setText("" + cart_count_adlt);
                }
            }
        });

        add_qyt_chld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart_count_chld++;
                if (cart_count_chld > 0) {
                    qty_txt_chld.setText("" + cart_count_chld);
                    sub_qyt_chld.setEnabled(true);
                } else {
                    sub_qyt_chld.setEnabled(false);
                }
            }
        });

        sub_qyt_chld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart_count_chld--;
                if (cart_count_chld < 0) {
                    qty_txt_chld.setText("" + 0);
                    sub_qyt_chld.setEnabled(false);
                } else {
                    add_qyt_chld.setEnabled(true);
                    qty_txt_chld.setText("" + cart_count_chld);
                }
            }
        });

        add_qyt_infant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart_count_infant++;
                if (cart_count_infant > 0) {
                    qty_txt_infant.setText("" + cart_count_infant);
                    sub_qyt_infant.setEnabled(true);
                } else {
                    sub_qyt_infant.setEnabled(false);
                }
            }
        });

        sub_qyt_infant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart_count_infant--;
                if (cart_count_infant < 0) {
                    qty_txt_infant.setText("" + 0);
                    sub_qyt_infant.setEnabled(false);
                } else {
                    add_qyt_infant.setEnabled(true);
                    qty_txt_infant.setText("" + cart_count_infant);
                }
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty_txt_adlt.getText().toString().equals("0") && qty_txt_senr.getText().toString().equals("0") && qty_txt_chld.getText().toString().equals("0") && qty_txt_infant.getText().toString().equals("0")) {
                } else {
                    Fragment fragment = new TicketInfo();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fram1, fragment);
                    fragmentTransaction.addToBackStack(TicketInfo.class.getSimpleName());
                    fragmentTransaction.commit();
                }
            }
        });
    }
}
