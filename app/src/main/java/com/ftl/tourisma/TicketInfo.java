package com.ftl.tourisma;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.adapters.CustomRecyclerAdapterForMembers;
import com.ftl.tourisma.adapters.TicketInfoAdapter;
import com.ftl.tourisma.minterface.RecyclerMembersListener;
import com.ftl.tourisma.models.TicketList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skpissay on 6/13/2017.
 */

public class TicketInfo extends Fragment implements RecyclerMembersListener {

    public static final String OBJ_PLACE = "OBJ_PLACE";
    private static final String CART_COUNT_ADLT = "Adult";
    private static final String CART_COUNT_SENR = "Senior";
    private static final String CART_COUNT_CHLD = "Child";
    private static final String CART_COUNT_INFANT = "Infant";
    Integer cart_count_adlt = 0;
    Integer cart_count_senr = 0;
    Integer cart_count_chld = 0;
    Integer cart_count_infant = 0;
    View view;
    MainActivity mainActivity;
    ImageView img_close;
    TextView txtTitle, info_txt, continue_btn;
    RecyclerView ticket_info_recycler_view;
    TicketInfoAdapter ticketInfoAdapter;
    ArrayList<TicketList> ticketLists = new ArrayList<>();
    LinearLayoutManager m_cLayoutManager;
    CustomRecyclerAdapterForMembers m_cRecycAdPlaces;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    RecyclerView m_cRecycPlaces;
    ImageView m_cBackImg;
    private boolean m_cLoading = true;
    private List<String> listMenbers;

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

        listMenbers = new ArrayList<>();
        Bundle mIntent = getArguments();
        cart_count_adlt = mIntent.getInt(CART_COUNT_ADLT);
        checkAndUpdateValues(cart_count_adlt, CART_COUNT_ADLT);
        cart_count_chld = mIntent.getInt(CART_COUNT_CHLD);
        checkAndUpdateValues(cart_count_chld, CART_COUNT_CHLD);
        cart_count_infant = mIntent.getInt(CART_COUNT_INFANT);
        checkAndUpdateValues(cart_count_infant, CART_COUNT_INFANT);
        cart_count_senr = mIntent.getInt(CART_COUNT_SENR);
        checkAndUpdateValues(cart_count_senr, CART_COUNT_SENR);
        init(view);

//        //setting adapter and layout manager
//        ticketInfoAdapter = new TicketInfoAdapter(getActivity(), ticketLists);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        ticket_info_recycler_view.setLayoutManager(mLayoutManager);
//        ticket_info_recycler_view.setAdapter(ticketInfoAdapter);

        return view;
    }

    private void init(View view) {
        m_cRecycPlaces = (RecyclerView) view.findViewById(R.id.ticket_info_recycler_view);
        m_cLayoutManager = new LinearLayoutManager(mainActivity);
        m_cLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        m_cRecycPlaces.setLayoutManager(m_cLayoutManager);
        m_cRecycPlaces.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = m_cLayoutManager.getChildCount();
                    totalItemCount = m_cLayoutManager.getItemCount();
                    pastVisiblesItems = m_cLayoutManager.findFirstVisibleItemPosition();

//                    int page = totalItemCount / 15;
                    if (m_cLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            m_cLoading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
//                            int lpage = page + 1;
//                            page = lpage;
//                            doPagination(lpage);
                        }
                    }
                }
            }
        });

        m_cRecycAdPlaces = new CustomRecyclerAdapterForMembers(mainActivity, listMenbers, this);
        m_cRecycPlaces.setAdapter(m_cRecycAdPlaces);

    }

    private void checkAndUpdateValues(Integer pSize, String pValue) {
        for (int i = 0; i < pSize; i++)
            listMenbers.add(pValue);
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

    @Override
    public void onInfoClick(int pPostion, String pMember, View pView) {
    }
}
