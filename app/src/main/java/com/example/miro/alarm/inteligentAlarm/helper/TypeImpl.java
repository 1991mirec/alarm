package com.example.miro.alarm.inteligentAlarm.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.miro.alarm.R;
import com.example.miro.alarm.inteligentAlarm.adapters.TimeAlarmSettingsAdapter;
import com.example.miro.alarm.inteligentAlarm.enums.Type;

import java.io.Serializable;

/**
 * Created by Miro on 11/26/2016.
 */

public class TypeImpl implements Serializable{

    private Type type;

    public TypeImpl(final Type type) {
        this.type = type;
    }

    public TypeImpl(final TypeImpl type) {
        this.type = type.type;
    }

    public Type getType() {
        return type;
    }

    public View setVisual(final LayoutInflater inflater, final ViewGroup parent, final TimeAlarmSettingsAdapter.TypeHolder typeHolder){
        View row = inflater.inflate(R.layout.two_text_fields,parent,false);
        typeHolder.changingText = (TextView) row.findViewById(R.id.changingTxtView_twoFields);
        typeHolder.mainText = (TextView) row.findViewById(R.id.mainTxtView_twoFields);

        final Context context = inflater.getContext();
        set(typeHolder, context);
        return row;
    }

    public void set(final TimeAlarmSettingsAdapter.TypeHolder typeHolder, final Context context) {
        final String typeInString = Utils.typeToString(type, context);

        typeHolder.changingText.setText(typeInString);
        typeHolder.mainText.setText(context.getText(R.string.type));
    }

    public void setType(int type) {
        switch (type){
            case 0: this.type = Type.SOUND;
                break;
            case 1:this.type = Type.VIBRATION;
                break;
            case 2:this.type = Type.BOTH;
                break;
        }
    }
}
