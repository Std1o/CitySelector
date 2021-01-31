package com.stdio.cityselector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    HashMap<Character, ArrayList<String>> map = new HashMap<Character, ArrayList<String>>();
    AutoCompleteTextView mAutoCompleteTextView;
    AutoCompleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCities();
        char firstChar = 'а';
        for (int i = 0; i < 33; i++) {
            map.put((char)(firstChar+i), new ArrayList<String>());
        }

        mAutoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int resource = android.R.layout.simple_dropdown_item_1line;
                Context context = MainActivity.this;
                if (charSequence.toString().isEmpty()) {
                    adapter = new AutoCompleteAdapter(context, resource, android.R.id.text1, new ArrayList<String>());
                } else {
                    ArrayList<String> cities = map.get(charSequence.toString().toLowerCase().charAt(0));
                    adapter = new AutoCompleteAdapter(context, resource, android.R.id.text1, cities);
                }
                mAutoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getCities() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.hh.ru/areas";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray array;

                        try {
                            array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONArray areas = array.getJSONObject(i).getJSONArray("areas");
                                for (int j = 0; j < areas.length(); j++) {
                                    String areaName = areas.getJSONObject(j).getString("name");
                                    if (!areaName.contains(" ")) {
                                        ArrayList<String> moscowList = map.get(areaName.toLowerCase().charAt(0));
                                        moscowList.add(areaName);
                                        map.put(areaName.toLowerCase().charAt(0), moscowList);
                                    }
                                    JSONArray cities = areas.getJSONObject(j).getJSONArray("areas");
                                    for (int k = 0; k < cities.length(); k++) {
                                        String name = cities.getJSONObject(k).getString("name");
                                        ArrayList<String> citiesList = map.get(name.toLowerCase().charAt(0));
                                        if (citiesList != null) {
                                            citiesList.add(name);
                                            map.put(name.toLowerCase().charAt(0), citiesList);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
}
