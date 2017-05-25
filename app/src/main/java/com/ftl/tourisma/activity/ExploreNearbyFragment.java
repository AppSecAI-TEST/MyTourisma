package com.ftl.tourisma.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ftl.tourisma.R;
import com.ftl.tourisma.database.Nearby;

import java.util.ArrayList;


public class ExploreNearbyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ExploreNearbyFragment";
    private View view;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainTabHostFragment.lastPage = 0;
        replaceFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new, container, false);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onClick(View view) {
    }

    public void replaceFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, new HomeFragment()).commit();
    }

    public void replacePlaceDetailsFragment(String placeId, String location) {
        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);
        bundle.putString("location", location);
        SearchResultPlaceDetailsFragment searchResultPlaceDetailsFragment = new SearchResultPlaceDetailsFragment();
        searchResultPlaceDetailsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, searchResultPlaceDetailsFragment).addToBackStack(SearchResultPlaceDetailsFragment.class.getSimpleName()).commit();
    }

    public void replaceSearchFragment() {
        Fragment fragment = new SearchFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fram1, fragment);
        fragmentTransaction.addToBackStack(SearchFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    public void replaceLocationFragment() {
        Fragment fragment = new SearchLocationFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fram1, fragment);
        fragmentTransaction.addToBackStack(SearchLocationFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    public void replaceSearchResultFragment(ArrayList<Nearby> nearbies1, String search, boolean isForCategory) {
        SearchResultFragmentFragment searchResultFragmentFragment = SearchResultFragmentFragment.NewInstance(nearbies1, search, isForCategory);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, searchResultFragmentFragment, SearchResultFragmentFragment.class.getSimpleName()).addToBackStack(SearchResultFragmentFragment.class.getSimpleName()).commit();
    }
}



