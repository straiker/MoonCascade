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
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class WeatherDetailActivity extends ActionBarActivity {
    public static final String myPrefs = "MyPrefs" ;
    public static final String REQUEST_URL= "http://www.ilmateenistus.ee/ilma_andmed/xml/forecast.php";
    public static SharedPreferences prefs = null;

    Document doc;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        new DownloadXML().execute(REQUEST_URL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent setPrefs = new Intent(this, UserLocationActivity.class);
        Intent list = new Intent(this, ListAllDatesActivity.class);

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(setPrefs);
            return true;
        } else if(id == R.id.forecast){
            startActivity(list);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(WeatherDetailActivity.this);
            // Set progressbar title
            pDialog.setTitle("Fetching data!");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();

            //fillMap();
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
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            TextView nameField = (TextView) findViewById(R.id.name);
            ImageView img = (ImageView) findViewById(R.id.img);
            TextView temp = (TextView) findViewById(R.id.temperatuur);
            TextView windText = (TextView) findViewById(R.id.wind);

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(d);

            XPath xPath =  XPathFactory.newInstance().newXPath();

            prefs = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
            String userLocation = prefs.getString("userLocation",null);

            String day = String.format("//forecast[@date='%s']//place[name[text()='%s']]", date, userLocation);
            String wind = String.format("//wind[name[text()='%s']]", userLocation);
            String text = String.format("//forecast[@date='%s']//text",date);

            NodeList nodeListDay;
            NodeList nodeListWind;

            // Node nams
            String tempmin = "tempmin";
            String tempmax = "tempmax";
            String p = "phenomenon";
            String name = "name";
            String sMin = "speedmin";
            String sMax = "speedmax";

            try {
                nodeListDay = (NodeList) xPath.compile(day).evaluate(doc, XPathConstants.NODESET);

                if(nodeListDay.getLength() == 0){
                    nameField.setText(String.format("Antud puupäeva - %s - kohta puudub ilmainfo!", date));
                    temp.setText(" ");
                    windText.setText(" ");
                }

                for (int i = 0; i < nodeListDay.getLength(); i++) {
                    Node n = nodeListDay.item(i);
                    NodeList nl = n.getChildNodes();

                    for (int k = 0; k < nl.getLength(); k++){
                        Node node = nl.item(k);
                        if(node.getNodeType() != Node.TEXT_NODE){
                            String nName = node.getNodeName();
                            String val = node.getFirstChild().getNodeValue();

                            if(nName.equals(p)){
                                img.setImageResource(PhenomenonData.getPhenomenon(val));
                            }
                            if(nName.equals(name)){
                                nameField.setText(val);
                            }
                            if(nName.equals(tempmin)){
                                temp.setText(temp.getText() + val);
                            }
                            if(nName.equals(tempmax)){
                                temp.setText(temp.getText() + " ... " + val);
                            }
                        }
                    }
                }
                /* parse nodes for wind  */
                nodeListWind = (NodeList) xPath.compile(wind).evaluate(doc, XPathConstants.NODESET);

                if(nodeListWind.getLength() == 0){
                    windText.setText(windText.getText() + "N/A");
                }

                for(int a = 0; a < nodeListWind.getLength(); a++){
                    Node nWind = nodeListWind.item(a);
                    NodeList nlWind = nWind.getChildNodes();

                    for (int b = 0; b < nlWind.getLength(); b++){
                        Node nodeWind = nlWind.item(b);
                        if(nodeWind.getNodeName().equals(sMin)){
                            windText.setText(windText.getText() + nodeWind.getFirstChild().getNodeValue());
                        }
                        if(nodeWind.getNodeName().equals(sMax)){
                            windText.setText(windText.getText() + " ... " + nodeWind.getFirstChild().getNodeValue());
                        }
                    }
                }

                NodeList textNodes = (NodeList) xPath.compile(text).evaluate(doc, XPathConstants.NODESET);
                TextView textField = (TextView) findViewById(R.id.text);

                for(int t = 0; t < textNodes.getLength(); t++){
                    Node textNode = textNodes.item(t);
                    if(t==0){
                        textField.setText("Öösel: " + textNode.getFirstChild().getNodeValue());
                    }
                    if(t==1){
                        textField.setText("\n"+textField.getText()+"\n"+"\n"+"Päeval: " + textNode.getFirstChild().getNodeValue());
                    }
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
        }
    }



}
