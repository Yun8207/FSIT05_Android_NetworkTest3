package tw.alex.networktest3;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private File sdroot, uploadFile;
    private TextView mesg;
    private RequestQueue queue;

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
        mesg = findViewById(R.id.message);
        queue = Volley.newRequestQueue(this);

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
                "http://192.168.201.160:8080/JavaEE/Alex01",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mesg.setText(response);
                    }
                },
                null
        );
        queue.add(request);
    }
}
