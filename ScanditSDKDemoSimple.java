//

package com.example.andrewmc.cart_0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrewmc on 15-09-29.
 */
public class ScanditSDKDemoSimple extends Activity implements ScanditSDKOnScanListener {

    // The main object for recognizing a displaying barcodes.
    private ScanditSDK mBarcodePicker;
    public String start_et;
    String url = "http://10.0.1.120/take_start.php";
    String url_barcode = "http://10.0.1.120/read_barcode2.php";
    String start_command;
    String barcode;
    String weight;
    public static final String WEIGHT = "Weight";
    ArrayList<HashMap<String, String>> Item_List;

    // Enter your Scandit SDK App key here.
    // Your Scandit SDK App key is available via your Scandit SDK web account.
    public static final String sScanditSdkAppKey = "vryU1ocpVJsRvl/1jt9Fb3EhsnI7sd7VbBbF6yEsWJc";

    Toast mToast = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }

    @Override
    protected void onPause() {
        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();

        super.onPause();
    }

    @Override
    protected void onResume() {
        // Once the activity is in the foreground again, restart scanning.
        mBarcodePicker.startScanning();
        super.onResume();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // We instantiate the automatically adjusting barcode picker that will
        // choose the correct picker to instantiate. Be aware that this picker
        // should only be instantiated if the picker is shown full screen as the
        // legacy picker will rotate the orientation and not properly work in
        // non-fullscreen.
        ScanditSDKAutoAdjustingBarcodePicker picker = new ScanditSDKAutoAdjustingBarcodePicker(
                this, sScanditSdkAppKey, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);

        // Add both views to activity, with the scan GUI on top.
        setContentView(picker);
        mBarcodePicker = picker;

        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.addOnScanListener(this);
    }

    /**
     *  Called when a barcode has been decoded successfully.
     */
    public void didScan(ScanditSDKScanSession session) {
        String message = "";
        for (ScanditSDKCode code : session.getNewlyDecodedCodes()) {
            String data = code.getData();
            // truncate code to certain length
            String cleanData = data;
            if (data.length() > 30) {
                cleanData = data.substring(0, 25) + "[...]";
            }
            if (message.length() > 0) {
                message += "\n\n\n";
            }
            message += cleanData;
            //message += "\n\n(" + code.getSymbologyString() + ")";
        }
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
        boolean start = message.contains("Cart 1");

        if(start) {
            start_et = "2";
            start_command = start_et;

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //start_et.setText("");
                            Toast.makeText(getApplicationContext(),
                                    "Command sent successfully.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),
                            "Failed to send command", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("start_command", start_command);

                    return params;
                }
            };

            // Adding request to request queue
            MyApplication.getInstance().addToReqQueue(postRequest);

        }

        //barcode = "\"" + message + "\"";
        barcode = message;

        if(!start) {

            StringRequest postRequest = new StringRequest(Request.Method.POST, url_barcode,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //start_et.setText("");
                            Toast.makeText(getApplicationContext(),
                                    "Barcode sent successfully.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),
                            "Failed to send barcode", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("barcode", barcode);

                    return params;
                }
            };

            // Adding request to request queue
            MyApplication.getInstance().addToReqQueue(postRequest);


                JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url_barcode, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int success = response.getInt("successes");

                                   if (success == 1) {
                                        JSONArray ja = response.getJSONArray("orders");

                                        for (int i = 0; i < ja.length(); i++) {

                                            JSONObject jobj = ja.getJSONObject(i);
                                            HashMap<String, String> item = new HashMap<String, String>();
                                            item.put(WEIGHT, jobj.getString(WEIGHT));


                                       } // for loop ends

                                        weight = WEIGHT;

                                        Toast.makeText(getApplicationContext(),
                                                weight,
                                                Toast.LENGTH_LONG).show();


                                    }
                                     else {
                                       Toast.makeText(getApplicationContext(),
                                               "Well, shit.",
                                               Toast.LENGTH_LONG).show();
                                   }
                                     // if ends

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                // Adding request to request queue
                MyApplication.getInstance().addToReqQueue(jreq);


        }

    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }





}