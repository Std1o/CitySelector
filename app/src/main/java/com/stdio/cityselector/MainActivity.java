package com.stdio.cityselector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

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
    private RecyclerView rv;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCities();
        initRecyclerView();
        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        setOnQueryTextListener();
        char firstChar = 'а';
        for (int i = 0; i < 33; i++) {
            map.put((char)(firstChar+i), new ArrayList<String>());
        }
    }

    private void setOnQueryTextListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    setAdapter(new ArrayList<String>());
                } else if (map.containsKey(s.toLowerCase().charAt(0))) {
                    setAdapter(getFilteredList(s.toLowerCase()));
                }
                return false;
            }
        });
    }

    private ArrayList<String> getFilteredList(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        ArrayList<String> cities = map.get(query.toLowerCase().charAt(0));
        for (String s : cities) {
            if (s.toLowerCase().contains(query)) {
                filteredList.add(s);
            }
        }
        return filteredList;
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

    private void initRecyclerView() {
        rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }


    private void setAdapter(ArrayList<String> dataList) {
        RVAdapter adapter = new RVAdapter(dataList, this);
        rv.setAdapter(adapter);
    }
}
