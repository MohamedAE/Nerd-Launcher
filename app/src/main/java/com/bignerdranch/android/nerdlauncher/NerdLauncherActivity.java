package com.bignerdranch.android.nerdlauncher;

import android.app.Fragment;

/*Hosts single fragment (NerdLauncherFragment*/
public class NerdLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}