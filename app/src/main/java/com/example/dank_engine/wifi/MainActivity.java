package com.example.dank_engine.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.dank_engine.wifi.R.layout.activity_main;
import static java.lang.Double.NaN;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    HashMap<String, Integer> wifi_info = new HashMap<>();
    HashMap<String, Integer> rp0 = new HashMap<>();
    HashMap<String, Integer> rp1 = new HashMap<>();
    HashMap<String, Integer> rp2 = new HashMap<>();
    HashMap<String, Integer> rp3 = new HashMap<>();
    HashMap<String, Integer> rp4 = new HashMap<>();

    List<String> rp0_bssids = Arrays.asList("f4:0f:1b:97:fd:30", "f4:0f:1b:93:8b:70", "f4:0f:1b:93:88:c0", "f4:0f:1b:ab:12:c0", "f4:0f:1b:93:a4:30", "f4:0f:1b:97:e1:00", "f4:0f:1b:97:e1:30");
    List<Integer> rp0_rssi = Arrays.asList(-64, -51, -65, -64, -65, -48, -53);

    List<String> rp1_bssids = Arrays.asList("f4:0f:1b:97:fd:30", "f4:0f:1b:ab:12:c0", "f4:0f:1b:93:a4:30", "f4:0f:1b:97:e1:30", "f4:0f:1b:93:88:c0", "f4:0f:1b:97:e1:00", "f4:0f:1b:97:f9:70");
    List<Integer> rp1_rssi = Arrays.asList(-59, -59, -58, -47, -58, -50, -63);

    List<String> rp2_bssids = Arrays.asList("f4:0f:1b:97:e1:30", "f4:0f:1b:93:88:c0", "f4:0f:1b:97:f9:70", "f4:0f:1b:93:a4:30", "f4:0f:1b:97:e1:00", "7c:0e:ce:0e:98:00", "f4:0f:1b:97:f9:e0", "f4:0f:1b:93:8b:70", "f4:0f:1b:ab:12:c0");
    List<Integer> rp2_rssi = Arrays.asList(-35, -54, -59, -63, -59, -65, -62, -62, -51);

    List<String> rp3_bssids = Arrays.asList("f4:0f:1b:97:fd:30", "f4:0f:1b:ab:12:c0", "f4:0f:1b:93:88:c0", "f4:0f:1b:97:e1:00", "00:23:eb:3a:14:f0", "f4:0f:1b:93:8b:70", "f4:0f:1b:97:e1:30");
    List<Integer> rp3_rssi = Arrays.asList(-54, -55, -64, -44, -57, -61, -49);

    List<String> rp4_bssids = Arrays.asList("f4:0f:1b:ab:12:c0", "f4:0f:1b:97:e1:00", "f4:0f:1b:97:f9:70", "f4:0f:1b:93:88:c0", "f4:0f:1b:97:e1:30");
    List<Integer> rp4_rssi = Arrays.asList(-46, -64, -65, -58, -39);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        Button locate = findViewById(R.id.locate);
        Button setPoint = findViewById(R.id.set_ref);
        Button scan = findViewById(R.id.scan);
        locate.setOnClickListener(this);
        setPoint.setOnClickListener(this);
        scan.setOnClickListener(this);
        updateLoc();

    }

    @Override
    public void onClick(View view) {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;

        List<HashMap> refPoint = new ArrayList<>();
        List<Double> euciledian = new ArrayList<>();

        rp0 = make_rp(rp0_bssids, rp0_rssi);
        rp1 = make_rp(rp1_bssids, rp1_rssi);
        rp2 = make_rp(rp2_bssids, rp2_rssi);
        rp3 = make_rp(rp3_bssids, rp3_rssi);
        rp4 = make_rp(rp4_bssids, rp4_rssi);

        refPoint.add(rp0);
        refPoint.add(rp1);
        refPoint.add(rp2);
        refPoint.add(rp3);
        refPoint.add(rp4);

        switch (view.getId()) {
            case R.id.locate:
                TextView location = findViewById(R.id.location);
                wifiManager.startScan();
                List<ScanResult> scanRef = wifiManager.getScanResults();

                for (ScanResult result : scanRef) {
                    if (!result.SSID.equals("eduroam")) {
                    } else {
                        wifi_info.put(result.BSSID, result.level);
                    }
                }

                for (HashMap element : refPoint) {
                    double compare_res = comparePoints(element, wifi_info);
                    euciledian.add(compare_res);
                }
                Log.i("loc", String.valueOf(euciledian));

                Double min = euciledian.get(0);
                int index = 0;
                for (int i = 0; i < euciledian.size(); i++) {
                    if (min > euciledian.get(i) && euciledian.get(i) != 0) {
                        min = euciledian.get(i);
                        index = i;
                    }
                }
                location.setText(String.valueOf(index));
                Log.i("loc", String.valueOf(index));
                break;


            case R.id.scan:
                wifiManager.startScan();
                ArrayList<String> wifiBssid = new ArrayList<>();
                ArrayList<Integer> wifiRSSI = new ArrayList<>();

                List<ScanResult> scanResults = wifiManager.getScanResults();

                for (ScanResult result : scanResults) {
                    if (result.level < -65 || !result.SSID.equals("eduroam")) {
                    } else {
                        wifiBssid.add("\"" + result.BSSID + "\"");
                        wifiRSSI.add(result.level);
                    }
                }

                Log.i("scan", wifiBssid + "\n" + wifiRSSI);

                ListView list_bssid = findViewById(R.id.list_bssid);
                ListView list_rssi = findViewById(R.id.list_rssi);
                ArrayAdapter<String> bssid = new ArrayAdapter<String>(this, R.layout.list_main, wifiBssid);
                ArrayAdapter<Integer> rssi = new ArrayAdapter<Integer>(this, R.layout.list_main, wifiRSSI);
                list_bssid.setAdapter(bssid);
                list_rssi.setAdapter(rssi);
                break;
        }

    }

    public static double comparePoints(HashMap<String, Integer> p1, HashMap<String, Integer> p2) {
        double sum = 0;
        double cnt = 0;
        for (String list : p1.keySet()) {
            Integer p2Val = p2.get(list);
            if (p2Val == null) {
                continue;
            }
            double diff = p1.get(list) - p2Val;
            sum += diff * diff;
            cnt++;
        }
        double ratio = cnt / p1.size();
//        Log.i("check", ratio + ";       " + cnt + ";        " + p1.size());
        return (Math.sqrt(sum)) / ratio;
    }

    public HashMap<String, Integer> make_rp(List id, List rssi) {
        HashMap<String, Integer> rp = new HashMap<>();
        for (int i = 0; i < id.size(); i++) {
            rp.put(String.valueOf(id.get(i)), (Integer) rssi.get(i));
        }
        return rp;
    }

    public void updateLoc() {

        final List<Double> euciledian = new ArrayList<>();
        final Handler handler = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert wifiManager != null;

                List<HashMap> refPoint = new ArrayList<>();
                List<Double> euciledianAvg;

                rp0 = make_rp(rp0_bssids, rp0_rssi);
                rp1 = make_rp(rp1_bssids, rp1_rssi);
                rp2 = make_rp(rp2_bssids, rp2_rssi);
                rp3 = make_rp(rp3_bssids, rp3_rssi);
                rp4 = make_rp(rp4_bssids, rp4_rssi);

                refPoint.add(rp0);
                refPoint.add(rp1);
                refPoint.add(rp2);
                refPoint.add(rp3);
                refPoint.add(rp4);

                TextView location = findViewById(R.id.location);
                wifiManager.startScan();
                List<ScanResult> scanRef = wifiManager.getScanResults();

                for (ScanResult result : scanRef) {
                    if (!result.SSID.equals("eduroam")) {
                    } else {
                        wifi_info.put(result.BSSID, result.level);
                    }
                }

                for (HashMap element : refPoint) {
                    double compare_res = comparePoints(element, wifi_info);
                    euciledian.add(compare_res);
                }
//                Log.i("loc", String.valueOf(euciledian));

                if (euciledian.size() >= (refPoint.size())*10) {
                    euciledianAvg = rolling(refPoint, euciledian);

                    Log.i("loc", String.valueOf(euciledianAvg));

                    Double min = euciledianAvg.get(0);
                    int index = 0;
                    for (int i = 0; i < euciledianAvg.size(); i++) {
                        if (min > euciledianAvg.get(i) && euciledianAvg.get(i) != NaN) {
                            min = euciledianAvg.get(i);
                            index = i;
                        }
                    }
                    location.setText(String.valueOf(index));
                    Log.i("loc", String.valueOf(index));
                    euciledian.clear();
                }
                handler.postDelayed(this, 100);
            }
        };

        handler.post(runnableCode);

    }

    public List<Double> rolling(List<HashMap> ref, List<Double> euc){
        List<Double> avgList = new ArrayList<>();
        for (int i = 0; i < ref.size(); i++){
            double sum = 0;
            double avg = 0;
            int cnt  = 0;
            for(int j = 0; j < euc.size(); j++){
                if(j%ref.size() == i){
                    sum += euc.get(j);
                    cnt++;
                }
            }
//            Log.i("check", String.valueOf(cnt));
            avg = sum/cnt;
            avgList.add(avg);
        }
        return  avgList;
    }
}
