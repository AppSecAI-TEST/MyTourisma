package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.models.TicketList;

import java.util.ArrayList;

/**
 * Created by Vinay on 6/8/2017.
 */

public class TicketInfoAdapter extends RecyclerView.Adapter<TicketInfoAdapter.ViewHolder> {

    Activity activity;
    ArrayList<TicketList> ticketLists = new ArrayList<>();


    public TicketInfoAdapter(Activity activity, ArrayList<TicketList> ticketLists) {
        this.activity = activity;
        this.ticketLists = ticketLists;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_information_recycler, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


    }

    @Override
    public int getItemCount() {
        return ticketLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText promo_edtxt;
        TextView name_txt;

        public ViewHolder(View v) {
            super(v);
            promo_edtxt = (EditText) v.findViewById(R.id.promo_edtxt);
            name_txt = (TextView) v.findViewById(R.id.name_txt);
        }
    }
}



