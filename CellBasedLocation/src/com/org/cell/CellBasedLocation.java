package com.org.cell;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class CellBasedLocation extends Activity {
    /** Called when the activity is first created. */
	GsmCellLocation location;
	int cid,lac;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String network = tm.getNetworkOperator();
        String mcc = network.substring(0, 3);
        String mnc = network.substring(3);
        Log.e("Cell",mcc+" "+mnc);
        location = (GsmCellLocation) tm.getCellLocation();
        cid = location.getCid();
        lac = location.getLac();
        Log.e("Cell",cid+" "+lac);
        network = tm.getNetworkOperatorName();
        Log.e("cell",network);
        

    }
}