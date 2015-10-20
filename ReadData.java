package com.example.andrewmc.cart_0;

/**
 * Created by andrewmc on 15-09-23.
 */
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class ReadData extends ListActivity {

    String url = "http://10.0.1.120/read_allCommand.php";
    ArrayList<HashMap<String, String>> Item_List;
    ProgressDialog PD;
    ListAdapter adapter;

    // JSON Node names
    public static final String ITEM_ID = "Id";
    public static final String ITEM_START = "Start";
    public static final String ITEM_STOP = "Stop";
    public static final String ITEM_PUSH = "Push";
    public static final String ITEM_RETRIEVE = "Retrieve";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Item_List = new ArrayList<HashMap<String, String>>();

        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.setCancelable(false);

        getListView().setOnItemClickListener(new ListitemClickListener());

        ReadDataFromDB();
    }

    private void ReadDataFromDB() {

        PD.show();
        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("orders");

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jobj = ja.getJSONObject(i);
                                    HashMap<String, String> item = new HashMap<String, String>();
                                    item.put(ITEM_ID, jobj.getString(ITEM_ID));
                                    item.put(ITEM_START,
                                            jobj.getString(ITEM_START));
                                    item.put(ITEM_STOP, jobj.getString(ITEM_STOP));
                                    item.put(ITEM_PUSH, jobj.getString(ITEM_PUSH));
                                    item.put(ITEM_RETRIEVE, jobj.getString(ITEM_RETRIEVE));

                                    Item_List.add(item);

                                } // for loop ends

                                String[] from = { ITEM_ID, ITEM_START, ITEM_STOP, ITEM_PUSH, ITEM_RETRIEVE };
                                int[] to = { R.id.item_id, R.id.item_start, R.id.item_stop, R.id.item_push, R.id.item_retrieve };

                                adapter = new SimpleAdapter(
                                        getApplicationContext(), Item_List,
                                        R.layout.list_items2, from, to);

                                setListAdapter(adapter);

                                PD.dismiss();

                            } // if ends

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

    }



    //On List Item Click move to UpdateDelete Activity
    class ListitemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent modify_intent = new Intent(ReadData.this,
                    UpdateDeleteData.class);

            modify_intent.putExtra("item", Item_List.get(position));

            startActivity(modify_intent);

        }

    }
}