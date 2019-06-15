package com.turtle.weatherpie;
import android.os.AsyncTask;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONTokener;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
public class MainActivity extends AppCompatActivity {

/* No need for now
    private static final String TAG = "UsingThingspeakAPI";

    private static final String THINGSPEAK_CHANNEL_ID = "YOUR ID";
    private static final String THINGSPEAK_API_KEY = "YOUR KEY"; //GARBAGE KEY
    private static final String THINGSPEAK_API_KEY_STRING = "YOUR KEY";

    */
    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "field1"; //Temperature
    private static final String THINGSPEAK_FIELD2 = "field2";//Humidity
    private static final String THINGSPEAK_FIELD3 = "field3";//Light
    private static final String THINGSPEAK_FIELD4 = "field4";//Atomsphere
    /*
    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/feeds/last?";
    */
    //static my URL
    private static final String URL_FEED = "https://api.thingspeak.com/channels/802224/feeds/last";
    TextView tem,light,atom,humid,tim;
    Button b1;

    // https://api.thingspeak.com/channels/266256/feeds.json
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tem=(TextView)findViewById(R.id.textView1);
        humid=(TextView)findViewById(R.id.textView2);
        light=(TextView)findViewById(R.id.textView3);
        atom=(TextView)findViewById(R.id.textView4);
        tim=(TextView)findViewById(R.id.textView6);
        b1=(Button) findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new FetchThingspeakTask().execute();
                }
                catch(Exception e){
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        });
    }
    class FetchThingspeakTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            tim.setText("Fetching Data from Server.Please Wait...");
        }
        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(URL_FEED);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response) {
            if(response == null) {
                Toast.makeText(MainActivity.this, "Something is wrong...", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                double v1 = channel.getDouble(THINGSPEAK_FIELD1);
                double v2 = channel.getDouble(THINGSPEAK_FIELD2);
                double v3 = channel.getDouble(THINGSPEAK_FIELD3);
                double v4 = channel.getDouble(THINGSPEAK_FIELD4);
                String created_at = channel.getString("created_at");
                if(v1>0) {
                    tim.setText(created_at);
                    tem.setText("" + v1+"\u2103");
                    humid.setText("" + v2+ "%");
                    light.setText("" + v3+ " lx");
                    atom.setText("" + v4+ " Pa");
                }
    //                t1.setText("Created at: "+created_at+"\nTemperature: "+v1+" C\nHumidity: "+v2+"\nLight: "+v3+"\nAtomsphere: "+v4);
                else
                    tim.setText("Result : NULL");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}