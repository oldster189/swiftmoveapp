package com.oldster.swiftmove.app.datatype;

import android.os.Bundle;

/**
 * Created by Old'ster on 28/7/2559.
 */
public class MutableInteger {
    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt("value", value);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedOnstanceState) {
        value = savedOnstanceState.getInt("value");

    }
}
