package se.ju.frjo1425student.waiterbotapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by jonas on 2017-12-07.
 */

public class dbHandler
{
    private static final String TAG = "DB_HANDLER";
    public static final String GET_URL = "http://get_order_url.com";
    public static final String SET_URL = "http://set_delivered_url.com";
    private static int[] spinnerChoices = {5, 10, 15, 30, 45, 60};

    public void dbHandler()
    {}

    public static class getDbRows extends AsyncTask<Void, String, InputStream>
    {
        public void getDbRows()
        {}

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected InputStream doInBackground(Void... params)
        {
            InputStream inStream = null;
            HttpURLConnection urlConnection = null;
            try
            {
                URL url = new URL(GET_URL);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                inStream = new BufferedInputStream(urlConnection.getInputStream());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return inStream;
        }

        @Override
        protected void onPostExecute(InputStream result)
        {
            SharedPreferences sharedPref =
                    ContextClass.getContext().getSharedPreferences("updateServerChoice", 0);

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    new getDbRows().execute();
                }
            }, 1000 * spinnerChoices[sharedPref.getInt("spinnerChoice", 0)]);

            try
            {
                readJsonStream(result);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void readJsonStream(InputStream in) throws IOException
        {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            try
            {
                //Sending the list of order items and updating listView in OrdersFragment
                OrdersFragment.updateOrderList(readOrderItemsArray(reader));
            }
            finally
            {
                reader.close();
            }
        }

        public List<OrderItem> readOrderItemsArray(JsonReader reader) throws IOException
        {
            List<OrderItem> orderItemsList = new ArrayList<>();
            reader.beginArray();
            while (reader.hasNext())
            {
                orderItemsList.add(readOrderItem(reader));
            }
            reader.endArray();
            return orderItemsList;
        }

        public OrderItem readOrderItem(JsonReader reader) throws IOException
        {
            int orderNumber = -1;
            int tableNumber = -1;
            String foodOrder = "";

            reader.beginObject();
            while (reader.hasNext())
            {
                String name = reader.nextName();
                if (name.equals("id"))
                {
                    orderNumber = reader.nextInt();
                }
                else if (name.equals("food"))
                {
                    foodOrder = reader.nextString();
                }
                else if (name.equals("tablenumber"))
                {
                    tableNumber = reader.nextInt();
                }
                else
                {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return new OrderItem(orderNumber, tableNumber, foodOrder);
        }
    }

    public static class setDbDelivered extends AsyncTask<String, String, String>
    {
        public setDbDelivered()
        {}

        @Override
        protected String doInBackground(String... params)
        {
            //String urlString = params[0];
            String id = "id=" + params[0];
            //String data = params[1];
            //not sure how to get result from POST method
            String inputLine;
            StringBuilder sb = new StringBuilder();


            HttpURLConnection urlConnection = null;
            try
            {
                URL url = new URL(SET_URL);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("POST");

                //Creating output stream and writing data to it
                OutputStream outStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outStream.write(id.getBytes());
                outStream.flush();

                //Creating input stream and reading data from it
                InputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader buffIn = new BufferedReader(new InputStreamReader(inStream));

                while ((inputLine = buffIn.readLine()) != null)
                    sb.append(inputLine);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d(TAG, "Trying to get result from http POST request");
            Log.d(TAG, "onPostExecute: " + result);
            new dbHandler.getDbRows().execute();
        }
    }
}
