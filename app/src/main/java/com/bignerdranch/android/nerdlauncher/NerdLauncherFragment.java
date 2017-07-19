package com.bignerdranch.android.nerdlauncher;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

    private static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*Set view*/
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);

        /*Set RecyclerView*/
        mRecyclerView =(RecyclerView) v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*Associate Fragment Adapter*/
        setupAdapter();
        return v;
    }

    /*Creates RecyclerView.Adapter and sets it to mRecyclerView
    * Sits between RecyclerView and data to be displayed*/
    private void setupAdapter() {
        /*Generate an Intent and specify two parameters: ACTION_MAIN and CATEGORY_LAUNCHER
        * MAIN: locate all activities with MAIN intent-filters - an application's entry point
        * MAIN intent filters may not include CATEGORY*/
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        /*Utilize PackageManager, return ResolveInfo object*/
        PackageManager pm = getActivity().getPackageManager();
        /*Generate a list of applications*/
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        /*Sort list*/
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString());
            }
        });

        Log.i(TAG, "Found " + activities.size() + " activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    /*Describes an item view within RecyclerView; meta data on its place in the RV*/
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ResolveInfo mResolveInfo;
        private ImageView mImageView;
        private TextView mNameTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.list_item_icon);
            mNameTextView = (TextView) itemView.findViewById(R.id.list_item_text);
            mNameTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();

            mNameTextView.setText(appName);
            mImageView.setImageDrawable(resolveInfo.loadIcon(pm));
        }

        @Override
        public void onClick(View v) {
            /*Get the activity's ComponentName (package name and class name together)*/
            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            /*Generate an explicit intent (at runtime) to launch specified app*/
            Intent i = new Intent(Intent.ACTION_MAIN).
                    setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    //Flag to force start specified app in new task
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }

    /*Provide binding from data set to view displayed in RV*/
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.nerd_launcher_list_item, parent, false);

            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder activityHolder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            activityHolder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

}