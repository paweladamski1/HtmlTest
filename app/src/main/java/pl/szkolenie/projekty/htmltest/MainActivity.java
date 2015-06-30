package pl.szkolenie.projekty.htmltest;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MainActivity extends ActionBarActivity {
    EditText imieTxt=null;
    EditText nazwiskoTxt=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imieTxt=(EditText)findViewById(R.id.ImieTxt);
        nazwiskoTxt=(EditText)findViewById(R.id.nazwiskoTxt);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnClickTestGetHttp(View view) {


        try {
            String t= "";
            ContentValues vals=new ContentValues();
            t = getResponseFromServer("http://192.168.56.1:8080/?id=89", vals);

            if(t!=null) {
                String[] tab = t.split(";");
                if(tab!=null && tab.length>1) {
                    imieTxt.setText(tab[0]);
                    nazwiskoTxt.setText(tab[1]);
                }
                else {
                    imieTxt.setText("");
                    nazwiskoTxt.setText("");
                }
            }

        } catch (Exception e) {
            imieTxt.setText("");
            nazwiskoTxt.setText("");
        }
    }

    public String getResponseFromServer(String address, ContentValues vals) throws Exception {

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpConnection(address, vals);

            InputStream is = httpURLConnection.getInputStream();
            return convertStreamToString(is);
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
    }


    public HttpURLConnection getHttpConnection(String address,
                                               ContentValues vals) throws Exception {

        URL servletURL;
        HttpURLConnection connection = null;

        servletURL = new URL(address);
        connection = (HttpURLConnection) servletURL.openConnection();

        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");

        OutputStream output = connection.getOutputStream();
        output.write(getQuery(vals).getBytes("UTF-8"));
        return connection;

    }

    private String getQuery(ContentValues vals) throws UnsupportedEncodingException
    {
        Set<Map.Entry<String, Object>> s=vals.valueSet();
        Iterator itr = s.iterator();
        StringBuilder result = new StringBuilder();

        boolean first = true;
        while(itr.hasNext())
        {
            if (first)
                first = false;
            else
                result.append("&");

            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void OnClickSendInfo(View view) {
        try {
            String t = "";
            ContentValues v = new ContentValues();
            v.put("imie", this.imieTxt.getText().toString());
            v.put("nazwisko", this.nazwiskoTxt.getText().toString());

            t = getResponseFromServer("http://192.168.56.1:8080/new_person.php", v);
            if (t!=null)
            {
                Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {

        }
    }
}
