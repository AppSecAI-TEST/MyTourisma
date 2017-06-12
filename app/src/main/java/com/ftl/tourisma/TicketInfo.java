package com.ftl.tourisma;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.adapters.TicketInfoAdapter;
import com.ftl.tourisma.models.TicketList;

import java.util.ArrayList;

/**
 * Created by Vinay on 6/8/2017.
 */

public class TicketInfo extends Fragment {

    View view;
    MainActivity mainActivity;
    ImageView img_close;
    TextView txtTitle, info_txt, continue_btn;
    RecyclerView ticket_info_recycler_view;
    TicketInfoAdapter ticketInfoAdapter;
    ArrayList<TicketList> ticketLists = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ticket_information, container, false);
        dcl_layout_variables(view);
        onClickListners();

//        //setting adapter and layout manager
//        ticketInfoAdapter = new TicketInfoAdapter(getActivity(), ticketLists);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        ticket_info_recycler_view.setLayoutManager(mLayoutManager);
//        ticket_info_recycler_view.setAdapter(ticketInfoAdapter);

        return view;
    }

    public void dcl_layout_variables(View view) {
        img_close = (ImageView) view.findViewById(R.id.img_close);
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        info_txt = (TextView) view.findViewById(R.id.info_txt);
        continue_btn = (TextView) view.findViewById(R.id.continue_btn);
        ticket_info_recycler_view = (RecyclerView) view.findViewById(R.id.ticket_info_recycler_view);
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

            }
        });
    }
}
