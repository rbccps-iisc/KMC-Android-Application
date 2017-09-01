package rbccps.kmc.connectivity.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by root on 16/3/16.
 */
public class NetworkSyncOperation extends AsyncTask<String, String, String> {

    HttpClient httpClient;
    HttpGet httpGet;
    HttpResponse response;
    BufferedReader reader;
    StringBuilder builder;
    String json;
    JSONObject obj;
    String token;
    String serverresponse;

    @Override
    protected String doInBackground(String... params) {

        try {
            httpClient = new DefaultHttpClient();
            httpGet = new HttpGet(
                    "http://KMCTest.sjri.res.in/RestAuthorizeClientSession.svc/GenTkn/" + "KMC022".trim());

            response = httpClient.execute(httpGet);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            builder = new StringBuilder();
            json = reader.readLine();
            obj = new JSONObject(json);
            token = obj.getString("GenerateTokenResult");
            System.out.println(token);
            Log.d("Http Post Response:", json);
            // write response to log
            Thread.sleep(1000);
            httpGet = new HttpGet(
                    "http://KMCTest.sjri.res.in/RestAuthorizeClientSession.svc/ImportData/" + token + "/" + "KMC022" + "/" + "KMC022,2016-3-17~14.44.00,30,30,0,0,12,12.929743,77.620225,A,A,2016-3-17~14.44.00");


            ///KMC151,2016-3-17~14.44.00,30,30,0,0,12,12.929743,77.620225,A,A,2016-3-17~14.44.00

            System.out.println("http://KMCTest.sjri.res.in/RestAuthorizeClientSession.svc/ImportData/" + token + "/" + "KMC022" + "/" + "KMC022,2016-3-17~14.44.00,30,30,0,0,12,12.929743,77.620225,A,A,2016-3-17~14.44.00");

            response = httpClient.execute(httpGet);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
                Log.d("Http Post Response:", line);
                if(line.contains("success")){
                    serverresponse = "success";
                } else if(line.contains("invalid")){
                    serverresponse = "invalid";
                } else if(line.contains("timeout")){
                    serverresponse = "timeout";
                }else if(line.contains("timeout")){
                    serverresponse = "timeout";
                }else{
                    serverresponse = "notknown";
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return serverresponse;
    }
}
