package tw.alex.networktest3;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private File sdroot, uploadFile;
    private TextView mesg;
    private RequestQueue queue;
    private ListView list;
    private MyAdapter myAdapter;
    private LinkedList<Food> data;
    private EditText input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }else{
            init();
        }

    }

    private void init(){
        sdroot = Environment.getExternalStorageDirectory();
        Log.v("alex", "" + sdroot);
        mesg = findViewById(R.id.message);
        list = findViewById(R.id.list);
        input = findViewById(R.id.input);
        data = new LinkedList<>();
        myAdapter = new MyAdapter(this);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoDetail(position);
            }
        });


        queue = Volley.newRequestQueue(this);

    }

    private void gotoDetail(int pos){
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("food", data.get(pos));
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    public void test1(View view) {
        new Thread(){
            @Override
            public void run() {
                postTest();
            }
        }.start();
    }

    private void postTest(){
        try {
            URL url = new URL("http://192.168.201.160:8080/JavaEE/Alex002");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            ContentValues values = new ContentValues();
            values.put("account", "alex");
            values.put("password", "1234");
            String query = queryString(values);

            OutputStream out = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(query);
            writer.flush();
            writer.close();

            conn.connect();
            conn.getInputStream();

            Log.v("alex", "OK");

        }catch(Exception e){
            Log.v("alex",e.toString());
        }
    }

    private String queryString(ContentValues data){
         Set<String> keys = data.keySet();
         StringBuffer sb = new StringBuffer();

         try {
             for (String key : keys) {
                 sb.append(URLEncoder.encode(key, "UTF-8"));
                 sb.append("=");
                 sb.append(URLEncoder.encode(data.getAsString(key),"UTF-8"));
                 sb.append("&");
             }

             sb.deleteCharAt(sb.length()-1);
             return sb.toString();
         }catch (Exception e){
             return null;

         }
    }

    public void test2(View view) {
        new Thread(){
            @Override
            public void run() {
                uploadFile();
            }
        }.start();
    }

    private void uploadFile(){
        try {
            uploadFile = new File(sdroot, "alex.txt");
            MultipartUtility mu = new MultipartUtility("http://192.168.201.160:8080/JavaEE/Alex11", "", "UTF-8");
            mu.addFilePart("upload", uploadFile);
            List<String> result = mu.finish();
            for (String line : result){
                Log.v("alex", line);

            }
        }catch(Exception e){
            Log.v("alex", e.toString());

        }
    }

    public void test3(View view) {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                "http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //mesg.setText(response);
                        parseJSON(response);
                    }
                },
                null
        );
        queue.add(request);
    }

    private void parseJSON(String json){
        try {
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                Food food = new Food();
                food.setName(row.getString("Name"));
                food.setAddress(row.getString("Address"));
                food.setTel(row.getString("Tel"));
                food.setHostwords(row.getString("HostWords"));
                food.setFeature(row.getString("FoodFeature"));
                food.setCoordinate(row.getString("Coordinate"));
                food.setPicurl(row.getString("PicURL"));
                data.add(food);
            }
            myAdapter.notifyDataSetChanged();
        }catch (Exception e){

        }


    }

    public void test4(View view) {
        final String send = input.getText().toString();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://192.168.201.160:8080/JavaEE/Alex002",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, null){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put("account", send);
                params.put("password", "123456");
                return params;
            }
        };

        queue.add(request);


    }

    public void test5(View view) {
        File upload = new File(sdroot, "alex.pdf");
        try {
            FileInputStream fin = new FileInputStream(upload);
            byte[] buf = new byte[(int)upload.length()];
            fin.read(buf);
            fin.close();
            final byte[] sendData = buf;


        VolleyMultipartRequest request =
                new VolleyMultipartRequest(Request.Method.POST, "http://192.168.201.160:8080/JavaEE/Alex11", new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.v("alex", new String(response.data));
                    }
                }, null){
                    @Override
                    protected Map<String, DataPart> getByteData() throws AuthFailureError {

                        HashMap<String, DataPart> params = new HashMap<>();
                        DataPart dataPart = new DataPart("alex4.pdf", sendData);
                        params.put("upload", dataPart);

                        return params;
                    }
                };

        queue.add(request);
        }catch (Exception e){
            Log.v("alex", e.toString());
        }

    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        MyAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.item, null);

            TextView name = view.findViewById(R.id.name);
            TextView tel = view.findViewById(R.id.tel);
            name.setText(data.get(position).getName());
            tel.setText(data.get(position).getTel());

            if (position % 2 == 0){
                view.setBackgroundColor(Color.YELLOW);

            }else{
                view.setBackgroundColor(Color.GRAY);
            }


            return view;
        }
    }
}
