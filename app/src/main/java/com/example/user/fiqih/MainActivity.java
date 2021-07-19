package com.example.user.fiqih;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ListView lV;
    ArrayList<HashMap<String, String>> data;
    AQuery aQuery;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lV = findViewById(R.id.ListView);
        data = new ArrayList<HashMap<String, String>>();
        ambilData();
    }

    private void ambilData() {

        String url = "http://192.168.100.4/fiqih/fiqih.php";

        aQuery = new AQuery(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading . .  .");
        aQuery.progress(progressDialog).ajax(url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                Toast.makeText(MainActivity.this, object, Toast.LENGTH_SHORT).show();
                if (object != null) {
                    try {
                        //apakah suksesnya true/false
                        JSONObject json = new JSONObject(object);
                        String pesan = json.getString("pesan");
                        String sukses = json.getString("sukses");

                        if (sukses.equalsIgnoreCase("true")) {
                            JSONArray jsonArray = json.getJSONArray("tatacara");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String jdl = jsonObject.getString("judul");
                                String id = jsonObject.getString("id");
                                String gambar = jsonObject.getString("gambar");

                                //datanya dimasukan ke hashmap
                                HashMap<String, String> map = new HashMap<>();
                                map.put("judul", jdl);
                                map.put("id", id);
                                map.put("gambar", gambar);
                                data.add(map);

                                setListAdapter(data);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void setListAdapter(final ArrayList<HashMap<String, String>> data) {
        ListViewAdapter adapter = new ListViewAdapter(this, data);
        lV.setAdapter(adapter);
        lV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = data.get(position);
                Intent a = new Intent(getApplicationContext(), DetailActivity.class);
                a.putExtra("id", map.get("id"));
                startActivity(a);
            }
        });
    }

    private class ListViewAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<HashMap<String, String>> data2;

        public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> data2) {
            this.activity = activity;
            this.data2 = data2;
        }

        @Override
        public int getCount() {
            return data2.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listitem, null);
            TextView jdl = convertView.findViewById(R.id.judulitem);
            TextView id = convertView.findViewById(R.id.idberita);
            CircleImageView img = convertView.findViewById(R.id.imgItem);

            HashMap<String, String> data = new HashMap<>();
            data = data2.get(position);

            jdl.setText(data.get("judul"));
            id.setText(data.get("id"));
            String urlfoto = "http://192.168.100.4/fiqih/gambar/";
            //tanda / artinya masuk
            Picasso.with(MainActivity.this).load(urlfoto+data.get("gambar")).error(R.drawable.p).into(img);

            return convertView;
        }
    }

}
