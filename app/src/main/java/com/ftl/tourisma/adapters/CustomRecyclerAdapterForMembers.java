package com.ftl.tourisma.adapters;

/**
 * Created by skpissay on 13-Jun-17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.minterface.RecyclerMembersListener;

import java.util.List;

public class CustomRecyclerAdapterForMembers extends RecyclerView.Adapter {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerMembersListener m_cClickListener;
    private static List<String> m_cObjJsonUsers;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForMembers(Context pContext, List<String> pJsonUsers,
                                           RecyclerMembersListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjJsonUsers = pJsonUsers;
        this.m_cClickListener = pListener;
    }

    @Override
    public int getItemCount() {
        return m_cObjJsonUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjJsonUsers.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_cell, parent, false);
            DataObjectHolder ldataObjectHolder = new DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_cell, parent, false);
            ProgressViewHolder lprogressViewHolder = new ProgressViewHolder(lView);
            vh = lprogressViewHolder;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DataObjectHolder) {

            try {
                ((DataObjectHolder) holder).fullNametxt.setText(m_cObjJsonUsers.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        EditText editBox;

        TextView fullNametxt;


        public DataObjectHolder(View itemView) {
            super(itemView);
            editBox = (EditText) itemView.findViewById(R.id.EDIT_MEMBER);
            fullNametxt = (TextView) itemView.findViewById(R.id.MEMBER_NAME);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}