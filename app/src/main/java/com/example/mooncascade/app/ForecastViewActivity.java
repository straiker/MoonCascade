package com.example.mooncascade.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class ForecastViewActivity extends ActionBarActivity {
    public String date;
    ProgressDialog pDialog;
    Document doc;
    public static final String REQUEST_URL= "http://www.ilmateenistus.ee/ilma_andmed/xml/forecast.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_view);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            date = extras.getString("DATE");
        }

        new DownloadXML().execute(REQUEST_URL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecast_view, menu);
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
            pDialog = new ProgressDialog(ForecastViewActivity.this);
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
            XPath xPath =  XPathFactory.newInstance().newXPath();

            String xDay = String.format("//forecast[@date = '%s']/day", date);
            String xNight = String.format("//forecast[@date = '%s']/night", date);

            NodeList nodeListDay;
            NodeList nodeListNight;

            ImageView img = (ImageView)findViewById(R.id.forecastPhenomenon);

            TextView dTemp = (TextView) findViewById(R.id.day_temp);
            TextView dInfo = (TextView) findViewById(R.id.day_info);
            TextView nTemp = (TextView) findViewById(R.id.night_temp);
            TextView nInfo = (TextView) findViewById(R.id.night_info);

            try {
                nodeListDay = (NodeList) xPath.compile(xDay).evaluate(doc, XPathConstants.NODESET);

                Log.v("Nodes day: ", String.valueOf(nodeListDay.getLength()));

                for (int i = 0; i < nodeListDay.getLength(); i++) {
                    Node n = nodeListDay.item(i);
                    NodeList nl = n.getChildNodes();

                    for(int a = 0; a < nl.getLength(); a++){
                        Node nDay = nl.item(a);
                        if (nDay.getNodeName().equals("phenomenon")){
                            img.setImageResource(PhenomenonData.getPhenomenon(nDay.getFirstChild().getNodeValue()));
                        }
                        if (nDay.getNodeName().equals("tempmin")){
                            dTemp.setText(dTemp.getText() + "Temperatuur: " + nDay.getFirstChild().getNodeValue());
                        }
                        if (nDay.getNodeName().equals("tempmax")){
                            dTemp.setText(dTemp.getText() + " ... " + nDay.getFirstChild().getNodeValue());
                        }
                        if (nDay.getNodeName().equals("text")){
                            dInfo.setText(nDay.getFirstChild().getNodeValue());
                        }
                    }
                }

                nodeListNight = (NodeList) xPath.compile(xNight).evaluate(doc, XPathConstants.NODESET);
                Log.v("Nodes night: ", String.valueOf(nodeListNight.getLength()));

                for (int i = 0; i < nodeListNight.getLength(); i++) {
                    Node s = nodeListDay.item(i);
                    NodeList ns = s.getChildNodes();

                    Log.v("Night nodes childs", String.valueOf(ns.getLength()));

                    for(int a = 0; a < ns.getLength(); a++) {
                        Node nNight = ns.item(i);
                        if (nNight.getNodeName().equals("tempmin")) {
                            nTemp.setText(nTemp.getText() + "Temperatuur: " + nNight.getFirstChild().getNodeValue());
                        }
                        if (nNight.getNodeName().equals("tempmax")) {
                            nTemp.setText(nTemp.getText() + " ... " + nNight.getFirstChild().getNodeValue());
                        }
                        if (nNight.getNodeName().equals("text")) {
                            nInfo.setText(nNight.getFirstChild().getNodeValue());
                        }
                    }
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }

}
