package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.R;

/**
 * Created by Vinay on 5/23/2017.
 */

public class TicketEventAdapter extends RecyclerView.Adapter<TicketEventAdapter.ViewHolder> {

    Activity activity;

    public TicketEventAdapter(Activity activity) {
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_event_recycler, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView place_txt, amt_txt;
        ImageView proceed_img;

        public ViewHolder(View v) {
            super(v);
            place_txt = (TextView) v.findViewById(R.id.place_txt);
            amt_txt = (TextView) v.findViewById(R.id.amt_txt);
            proceed_img = (ImageView) v.findViewById(R.id.proceed_img);
        }
    }
}