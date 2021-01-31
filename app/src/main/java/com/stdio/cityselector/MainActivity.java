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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    HashMap<String, Integer> countries = new HashMap<>();
    private ArrayList<String> countriesList = new ArrayList<>();
    AutoCompleteTextView mAutoCompleteTextView;
    AutoCompleteTextView tvCountry;
    AutoCompleteAdapter adapter;
    int country_id = 1;
    private final String access_token = "8b53cb558b53cb558b53cb55a38b25ff5888b538b53cb55eb4505f5df55f0c2ce7d60b4";
    private final String URL_FOR_COUNTRIES = "https://api.vk.com/method/database.getCountries?count=1000&lang=ru&need_all=1&v=5.126&&access_token=" + access_token;
    private final String URL_FOR_CITIES = "https://api.vk.com/method/database.getCities?count=1000&lang=ru&need_all=1&v=5.126&access_token=" + access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCountries();
        mAutoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        tvCountry = findViewById(R.id.tvCountry);
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (countries.containsKey(tvCountry.getText().toString())) {
                    country_id = countries.get(tvCountry.getText().toString());
                    int resource = android.R.layout.simple_dropdown_item_1line;
                    Context context = MainActivity.this;
                    if (charSequence.toString().isEmpty()) {
                        adapter = new AutoCompleteAdapter(context, resource, android.R.id.text1, new ArrayList<String>());
                        mAutoCompleteTextView.setAdapter(adapter);
                    } else {
                        getCities(charSequence.toString());
                    }
                } else {
                    mAutoCompleteTextView.setError("Указанная страна не найдена");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvCountry.addTextChangedListener(new TextWatcher() {
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
                    adapter = new AutoCompleteAdapter(context, resource, android.R.id.text1, countriesList);
                }
                tvCountry.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getCountries() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL_FOR_COUNTRIES;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray;

                        try {
                            jsonArray = new JSONObject(response).getJSONObject("response").getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                countriesList.add(jsonObject.getString("title"));
                                countries.put(jsonObject.getString("title"), jsonObject.getInt("id"));
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

    private void getCities(String query) {
        ArrayList<String> cities = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL_FOR_CITIES + "&q=" + query+"&country_id=" + country_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray;
                        try {
                            jsonArray = new JSONObject(response).getJSONObject("response").getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String city = jsonObject.getString("title");
                                if (jsonObject.has("region")) {
                                    city += ", " + jsonObject.getString("region");
                                }
                                if (!cities.contains(city)) {
                                    cities.add(city);
                                }
                            }
                            int resource = android.R.layout.simple_dropdown_item_1line;
                            Context context = MainActivity.this;
                            adapter = new AutoCompleteAdapter(context, resource, android.R.id.text1, cities);
                            mAutoCompleteTextView.setAdapter(adapter);
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
