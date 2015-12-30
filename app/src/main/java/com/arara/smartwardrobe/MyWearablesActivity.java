package com.arara.smartwardrobe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyWearablesActivity extends AppCompatActivity {

    List<Wearable> wearablesList;
    ListView lvWearables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wearables);

        wearablesList = new ArrayList<>();

        ArrayAdapter<Wearable> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wearablesList);
        Log.d("adapterSize", adapter.getCount() + "");
        lvWearables = (ListView) findViewById(R.id.lvWearables);
        lvWearables.setAdapter(adapter);
        lvWearables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wearable selectedWearable = (Wearable) lvWearables.getItemAtPosition(position);
                //Misc.showAlertMsg("Clicked on " + selectedWearable, "Ok", MyWearablesActivity.this);
                Intent intent = new Intent(MyWearablesActivity.this, ViewWearableActivity.class);
                intent.putExtra("selectedWearable", selectedWearable);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWearablesList();
    }

    private void loadWearablesList() {
        String wearablesOwner = getIntent().getExtras().getString("owner");
        Log.d("wearablesOwner", wearablesOwner);
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchUserWearableDataInBackground(new User(wearablesOwner), new Callback() {
            @Override
            public void done(ServerResponse serverResponse) {
                Log.d("serverResponseMyW", serverResponse.response);
                if (serverResponse.response.equals("error")) {
                    Misc.showAlertMsg("An error occurred while trying to connect.", "Ok", MyWearablesActivity.this);
                    finish();
                } else if (serverResponse.response.equals("no wearables")) {
                    Misc.showAlertMsg("No wearable found.", "Ok", MyWearablesActivity.this);
                    finish();
                } else {
                    buildWearablesList(serverResponse.response);
                    ((BaseAdapter) lvWearables.getAdapter()).notifyDataSetChanged();
                }
            }
        });
    }

    private void buildWearablesList(String wearablesData) {

        wearablesList.clear();

        Log.d("debugTest", "Started building");

        Log.d("wearablesData", wearablesData);

        List<String> wearableStrings = Arrays.asList(wearablesData.split("\\$"));

        for (String wearable: wearableStrings) {

            List<String> wearableData = Arrays.asList(wearable.split("#"));

            Wearable newWearable = new Wearable();

            newWearable.id = wearableData.get(0);
            newWearable.colors = (wearableData.get(1)).replace("&", ", ");
            newWearable.type = wearableData.get(2);
            newWearable.brand = wearableData.get(3);
            newWearable.description = wearableData.get(4);

            wearablesList.add(newWearable);
        }

        Log.d("friendshipsStrings", wearableStrings.toString());

        Log.d("debugTest", "Finished building");
    }
}
