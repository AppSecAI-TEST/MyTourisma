package com.ftl.tourisma;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;

/**
 * Created by Vinay on 5/23/2017.
 */

public class TicketEvent extends Fragment {

    View view;
    RelativeLayout edit_date;
    RecyclerView event_recycler;
    TextView continue_btn, txtTitle;
    ImageView img_close;
    MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ticket_event, container, false);
        dcl_layout_variable(view);
        onClickListners();

        //setting layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        event_recycler.setLayoutManager(linearLayoutManager);
        return view;
    }

    public void dcl_layout_variable(View view) {
        edit_date = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        event_recycler = (RecyclerView) view.findViewById(R.id.recycler_view);
        continue_btn = (TextView) view.findViewById(R.id.continue_btn);
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
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
                Fragment fragment = new TicketPersonalDetails();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment);
                fragmentTransaction.addToBackStack(TicketPersonalDetails.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });
    }
}
