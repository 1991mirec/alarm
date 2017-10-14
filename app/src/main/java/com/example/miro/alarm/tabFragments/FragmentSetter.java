package com.example.miro.alarm.tabFragments;

import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Miro on 11/22/2016.
 */

public interface FragmentSetter {

    LinearLayout initiateButtons();

    Button createButton(final String name);

    void removeButton(int id);
}
