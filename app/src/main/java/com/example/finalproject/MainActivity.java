package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catchData();
    }

    private void catchData() {
        String catchDataUrl = "https://api.jsonserve.com/UXaUce";
        ProgressDialog dialog = ProgressDialog.show(this, "加载中", "请稍候", true);
        arrayList = new ArrayList<>();  // 确保arrayList已初始化

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);

        new Thread(() -> {
            try {
                URL url = new URL(catchDataUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }

                JSONArray dataArray = new JSONArray(json.toString());

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);
                    String sitename = item.getString("sitename");
                    String uvi = item.getString("uvi");
                    String county = item.getString("county");
                    String datacreationdate = item.getString("datacreationdate");

                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("sitename", sitename);
                    dataMap.put("uvi", uvi);
                    dataMap.put("county", county);
                    dataMap.put("datacreationdate", datacreationdate);

                    arrayList.add(dataMap);
                }

                Log.d(TAG, "catchData: " + arrayList);

                runOnUiThread(() -> {
                    dialog.dismiss();
                    myAdapter.notifyDataSetChanged(); // 通知适配器数据已改变
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSitename, tvUvi, tvCounty, tvDataCreationDate;

            ViewHolder(View itemView) {
                super(itemView);
                tvSitename = itemView.findViewById(R.id.city);
                tvUvi = itemView.findViewById(R.id.uv);
                tvCounty = itemView.findViewById(R.id.du4);
                tvDataCreationDate = itemView.findViewById(R.id.date);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_data_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, String> item = arrayList.get(position);

            holder.tvSitename.setText("Site Name: " + item.get("sitename"));
            holder.tvUvi.setText("UV Index: " + item.get("uvi"));
            holder.tvCounty.setText("County: " + item.get("county"));
            holder.tvDataCreationDate.setText("Data Creation Date: " + item.get("datacreationdate"));
        }


        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}
