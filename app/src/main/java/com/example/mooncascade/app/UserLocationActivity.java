package com.example.mooncascade.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class UserLocationActivity extends ActionBarActivity {
    public static final String REQUEST_URL= "http://www.ilmateenistus.ee/ilma_andmed/xml/forecast.php";
    public static final String myPrefs = "MyPrefs" ;
    public static SharedPreferences prefs = null;
    public ArrayList<String> places;

    Document doc;
    ProgressDialog pDialog;
    ListView placesView;

    // Activity that sets the users location that the user selects from an array
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        Log.v("Activity","### Set prefs activity started");

        prefs = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);

        final Intent goBack = new Intent(this, WeatherDetailActivity.class);

        places = new ArrayList<String>();

        new DownloadXML().execute(REQUEST_URL);
        placesView = (ListView)findViewById(R.id.listView);

        placesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String val = places.get(i);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userLocation", val);
                editor.commit();
                startActivity(goBack);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent setPrefs = new Intent(this, UserLocationActivity.class);

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(setPrefs);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class DownloadXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(UserLocationActivity.this);
            // Set progressbar title
            pDialog.setTitle("Fetching locations!");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void args) {
            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expression = "//day/place/name";
            NodeList nodeList;

            try {
                nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    places.add(node.getFirstChild().getNodeValue());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UserLocationActivity.this, R.layout.list_row, places);
                placesView.setAdapter(arrayAdapter);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
        }
    }
}
