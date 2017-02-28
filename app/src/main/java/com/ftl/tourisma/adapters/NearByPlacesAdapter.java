//package com.ftl.tourisma.adapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.support.v7.widget.RecyclerView;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//
//import com.ftl.tourisma.FavouriteFragment;
//import com.ftl.tourisma.R;
//import com.ftl.tourisma.ShareFragmentActivity;
//import com.ftl.tourisma.custom_views.NormalTextView;
//import com.ftl.tourisma.database.Nearby;
//import com.ftl.tourisma.utils.Constants;
//import com.squareup.picasso.Picasso;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * Created by C162 on 25/10/16.
// */
//
//public class NearByPlacesAdapter extends RecyclerView.Adapter<NearByPlacesAdapter.ViewHolder> {
//
//    private int width, height;
//    private Context context;
//    private LayoutInflater layoutInflater;
//    private ArrayList<Nearby> arrayList = new ArrayList<>();
//    private SharedPreferences mPreferences;
//
//    public NearByPlacesAdapter(Context context, ArrayList<Nearby> arrayList, int height, int width, SharedPreferences mPreferences) {
//        this.context = context;
//        this.layoutInflater = LayoutInflater.from(context);
//        this.arrayList = arrayList;
//        this.mPreferences = mPreferences;
//        this.height = height;
//        this.width = width;
//
//    }
//
//    @Override
//    public NearByPlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = layoutInflater.inflate(R.layout.item_nearby, parent, false);
//        viewHolder = new ViewHolder(view);
//        return viewHolder;
//    }
//    ViewHolder viewHolder;
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.txtShare.setText(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Share"));
//        holder.txtFav.setText(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Favourite"));
//
//
//        String imageURL = Constants.IMAGE_URL + arrayList.get(position).getPlace_MainImage() + "&w=" + (width - 30);
////            Log.d("System out", imageURL);
////            imageLoader.displayImage(imageURL, holder.iv_nearby_explorer, options);
//
//        Picasso.with(context) //
//                .load(imageURL) //
//                .into(holder.iv_nearby_explorer);
//
//        holder.tv_near.setText(arrayList.get(position).getPlace_Name());
//        holder.txtCategory.setText(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Category") + ": " + arrayList.get(position).getCategory_Name()
//        );
//
//        holder.llView.setId(position);
//        holder.llView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              /*  ll_my_favourite1.setVisibility(View.GONE);
//                ll_my_favorite3.setVisibility(View.VISIBLE);
//                id = v.getId();
//                Category_Name = arrayList.get(position).getCategory_Name();
//                if (mHandler != null) {
//                    mHandler.removeCallbacks(mRunnable);
//                }
//                setDetailInfo(arrayList.get(v.getId()));
//                mNearby = arrayList.get(v.getId());*/
//            }
//        });
//        holder.iv_nearby_explorer.setId(position);
//        holder.iv_nearby_explorer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                ll_my_favourite1.setVisibility(View.GONE);
////                ll_my_favorite3.setVisibility(View.VISIBLE);
////                id = v.getId();
////                Category_Name = arrayList.get(position).getCategory_Name();
////                if (mHandler != null) {
////                    mHandler.removeCallbacks(mRunnable);
////                }
////                setDetailInfo(arrayList.get(v.getId()));
////                mNearby = arrayList.get(v.getId());
//            }
//        });
//        //TOdo
//        holder.rl_navigator.setId(position);
//        holder.rl_navigator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(arrayList.get(position).getPlace_Latitude()), Double.parseDouble(arrayList.get(position).getPlace_Longi()));
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//                context.startActivity(intent);
//                //  Utilities.toast(context, "In-Progress -> It will Navaigate on google map");
//            }
//        });
//       /* if (arrayList.get(position).getHoursOfOperations().get(0).getPOH_Start_Time().length() != 0) {
//            String _24HourTime = arrayList.get(position).getHoursOfOperations().get(0).getPOH_Start_Time();
//            String _24HourTime1 = arrayList.get(position).getHoursOfOperations().get(0).getPOH_End_Time();
//            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
//            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mma");
//            Date _24HourDt = null;
//            Date _24HourDt1 = null;
//            try {
//                _24HourDt = _24HourSDF.parse(_24HourTime);
//                _24HourDt1 = _24HourSDF.parse(_24HourTime1);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            holder.tv_timing.setText(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Timing") + ": " + _12HourSDF.format(_24HourDt).toString().replace("AM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "pm")) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).toString().replace("AM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "pm")));
//        }
//*/
//        if (arrayList.get(position).getFav_Id().equalsIgnoreCase("0")) {
////                holder.iv_favorite.setImageResource(R.drawable.like_icon);
//            holder.imgFav.setActivated(false);
//        } else {
//            holder.imgFav.setActivated(true);
////                holder.iv_favorite.setImageResource(R.drawable.like_icon_);
//        }
//
//        holder.rl_share.setId(position);
//        holder.rl_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent mIntent = new Intent(context, ShareFragmentActivity.class);
//                String share1 = Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "share1");
//                String share2 = Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "share2");
//                String share3 = Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "share3");
//
//                mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + arrayList.get(v.getId()).getPlace_Name() + "\" " + share3);
//                context.startActivity(mIntent);
////                        Intent intent = new Intent(Intent.ACTION_SEND);
////                        intent.setType("text/plain");
////                        startActivity(Intent.createChooser(intent, "Share with"));
//            }
//        });
//
//           /* holder.rl_navigator.setId(position);
//            holder.rl_navigator.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent mIntent = new Intent(context, com.ftl.tourisma.MapLocationFragment.class);
//                    mIntent.putExtra("placeName", arrayList.get(holder.rl_navigator.getId()).getPlace_Name());
//                    mIntent.putExtra("dist", arrayList.get(holder.rl_navigator.getId()).getDist());
//                    mIntent.putExtra("latitude", arrayList.get(holder.rl_navigator.getId()).getPlace_Latitude());
//                    mIntent.putExtra("longitude", arrayList.get(holder.rl_navigator.getId()).getPlace_Longi());
//                    mIntent.putExtra("mDirection", "no");
//                    mIntent.putExtra("address", arrayList.get(holder.rl_navigator.getId()).getPlace_Address());
//                    startActivity(mIntent);
//                }
//            });*/
//
//        holder.rl_fav.setId(position);
//        holder.rl_fav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int j = 0; j < arrayList.size(); j++) {
//                    if (v.getId() == j) {
//                        if (arrayList.get(j).getFav_Id().equalsIgnoreCase("0")) {
//                            holder.imgFav.setActivated(true);
////                                holder.iv_favorite.setImageResource(R.drawable.like_icon_);
//                            // addFavoriteCall(arrayList.get(j).getPlace_Id());
//                            //  mFlag = j;
//                        } else {
//                            holder.imgFav.setActivated(false);
////                                holder.imgFav.setImageResource(R.drawable.like_icon);
//                            // deleteFavoriteCall(arrayList.get(j).getFav_Id());
//                            // mFlag = j;
//                        }
//                    }
//                }
//            }
//        });
//
//
//        if (arrayList.get(position).getHoursOfOperations().size() > 0) {
////                holder.tv_ticket.setText(arrayList.get(position).getHoursOfOperations().get(0).getPOH_Charges() + "$");
//            holder.tv_ticket.setText(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Tickets") + ": " + arrayList.get(position).getHoursOfOperations().get(0).getPOH_Charges() + "");
//        }
//        holder.txtDistance.setText(arrayList.get(position).getDist() + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "KM"));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrayList.size();
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//
//        private ImageView iv_nearby_explorer, imgFav;
//        private NormalTextView tv_ticket, txtCategory, txtFav, txtDistance, tv_near, txtShare, tv_timing;
//        private LinearLayout llView;
//        private View container;
//        LinearLayout rl_fav, rl_navigator, rl_share;
//        ProgressBar progressBar;
//
//        public ViewHolder(View convertView) {
//            super(convertView);
//            iv_nearby_explorer = (ImageView) convertView.findViewById(R.id.iv_nearby_explorer);
//            imgFav = (ImageView) convertView.findViewById(R.id.imgFav);
////                iv_share_explorer = (ImageView) convertView.findViewById(R.id.iv_share_explorer);
//            tv_near = (NormalTextView) convertView.findViewById(R.id.tv_near);
//            tv_timing = (NormalTextView) convertView.findViewById(R.id.tv_timing);
//            tv_ticket = (NormalTextView) convertView.findViewById(R.id.tv_ticket);
//            txtShare = (NormalTextView) convertView.findViewById(R.id.txtShare);
//            txtFav = (NormalTextView) convertView.findViewById(R.id.txtFav);
//            txtDistance = (NormalTextView) convertView.findViewById(R.id.txtDistance);
////                container = convertView.findViewById(R.id.cv_search_result_adapter);
//            llView = (LinearLayout) convertView.findViewById(R.id.llView);
//            final RelativeLayout rlNearBy = (RelativeLayout) convertView.findViewById(R.id.rlNearBy);
//            rl_fav = (LinearLayout) convertView.findViewById(R.id.rl_fav);
//            rl_navigator = (LinearLayout) convertView.findViewById(R.id.rl_navigator);
//            rl_share = (LinearLayout) convertView.findViewById(R.id.rl_share);
////                ll_location_near_map = (LinearLayout) view.findViewById(R.id.ll_location_near_map);
////               iv_share_explorer = (ImageView) view.findViewById(R.id.iv_share_explorer);
//            txtCategory = (NormalTextView) convertView.findViewById(R.id.txtCategory);
//            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
//            iv_nearby_explorer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (height * 60) / 100));
//
//        }
//    }
//}
