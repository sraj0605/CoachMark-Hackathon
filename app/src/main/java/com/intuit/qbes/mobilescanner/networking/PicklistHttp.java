package com.intuit.qbes.mobilescanner.networking;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pdixit on 10/5/16.
 */
public class PicklistHttp {

    private static final String LOG_STR = "PicklistHttp";

    public byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ":with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        catch (Exception ex)
        {
            Log.e("PicklistHttp", "Failed to fetch URL: ", ex);
        }
        finally {
            connection.disconnect();
        }
        return null;
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public int putUrlString(String urlSpec, String body, String response) throws IOException
    {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = null;

        try
        {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(body);
            writer.close();

            InputStream in = connection.getInputStream();
            if (in == null)
            {
                //Todo: Throw exception
                return -1;
            }
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(in));

            String inputLine;
            while((inputLine = reader.readLine()) != null)
            {
                buffer.append(inputLine + "\n");
                if (buffer.length() == 0)
                {
                    // Stream was empty. No point in parsing.
                    //Todo: Throw exception
                    return -1;
                }
            }
            Log.d(LOG_STR, String.format("PUT RESPONSE:%s", buffer.toString()));

            if (response != null)
            {
                response = buffer.toString();
            }
            return connection.getResponseCode();

        }
        finally {
            connection.disconnect();
            if (reader != null)
            {
                reader.close();
            }
        }
    }
}
