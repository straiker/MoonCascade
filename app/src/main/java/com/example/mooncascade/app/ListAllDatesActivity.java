package com.example.mooncascade.app;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import android.app.ProgressDialog;
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
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ListAllDatesActivity extends ActionBarActivity {
    TextView textview;
    ListView info;
    ProgressDialog pDialog;
    Document doc;
    String date;

    ArrayList<String> dates;
    public static final String REQUEST_URL= "http://www.ilmateenistus.ee/ilma_andmed/xml/forecast.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_dates);

        dates = new ArrayList<String>();

        new DownloadXML().execute(REQUEST_URL);

        info = (ListView) findViewById(R.id.info);

        final Intent forecastView = new Intent(this, ForecastViewActivity.class);
        info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String val = dates.get(i);
                forecastView.putExtra("DATE", val);
                startActivity(forecastView);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_all_places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
    private class DownloadXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(ListAllDatesActivity.this);
            // Set progressbar title
            pDialog.setTitle("Loading Weather Info!");
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
                // Download the XML file
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void args) {
            textview = (TextView) findViewById(R.id.test);

            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expression = "//forecast";
            NodeList nodeList;
            try {
                nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nodes = nodeList.item(i);
                    date = nodes.getAttributes().getNamedItem("date").getNodeValue();
                    dates.add(date);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ListAllDatesActivity.this, R.layout.list_row, dates);
                info.setAdapter(arrayAdapter);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }
}
