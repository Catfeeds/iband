package com.manridy.iband.service;

import android.app.Activity;


import com.manridy.iband.view.OtaActivity;

import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * Created by jarLiao on 17/3/24.
 */

public class DfuService extends DfuBaseService{
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return OtaActivity.class;
    }
}
