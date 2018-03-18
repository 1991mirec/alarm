package com.example.miro.alarm.tabFragments;

import android.widget.LinearLayout;

/**
 * Created by Miro on 11/22/2016.
 */

public interface FragmentSetter {

    LinearLayout initiateButtons();

    void addButton();

    void removeButton(int id);
}
