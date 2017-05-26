package com.ftl.tourisma.postsync;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ftl.tourisma.R;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostSync extends AsyncTask<String, Integer, String> {

    private static final String TAG = "PostSync";
    static String resultString = "";
    Dialog dialog;
    Context context;
    ProgressBar progressbar;
    String action = "";
    private post_sync.ResponseHandler responseHandler;

    public PostSync(Context context, String action, post_sync.ResponseHandler responseHandler) {
        this.context = context;
        this.responseHandler = responseHandler;
        this.action = action;
    }

    public static String convertInputStreamToString(InputStream is) {
        BufferedReader reader;
        StringBuilder sb = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
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

    public static String invoke1(String url, String umid) {
        String s = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpParams httpParameters = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
        HttpClient client = new DefaultHttpClient(httpParameters);
        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams().setParameter("http.socket.timeout", new Integer(15000));
        client.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
        httpParameters.setBooleanParameter("http.protocol.expect-continue", false);
        HttpPost request = new HttpPost(url);
        request.getParams().setParameter("http.socket.timeout", new Integer(15000));
        Utils.Log(TAG, "invoke1 link : " + url);
        try {
            org.apache.http.Header[] headers = request.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                Utils.Log(TAG, "invoke1 headername : " + headers[i].getName() + ", value: " + headers[i].getValue());
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("json", umid));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                resultString = EntityUtils.toString(httpEntity).toString();
                Utils.Log(TAG, "invoke1 response is :" + resultString);
            }
            httpEntity = null;
            response = null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Utils.Log(TAG, "invoke1 OutOfMemoryError :" + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Utils.Log(TAG, "invoke1 Exception :" + e.getLocalizedMessage());
        }
        httppost = null;
        httpclient = null;
        return resultString;
    }

    public static String invoke2(String url, String json, String orderDetails) {
        String s = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpParams httpParameters = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
        HttpClient client = new DefaultHttpClient(httpParameters);
        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams().setParameter("http.socket.timeout", new Integer(50000));
        client.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
        httpParameters.setBooleanParameter("http.protocol.expect-continue", false);
        HttpPost request = new HttpPost(url);
        request.getParams().setParameter("http.socket.timeout", new Integer(50000));
        Utils.Log(TAG, "link : " + url);
        try {
            org.apache.http.Header[] headers = request.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                Utils.Log(TAG, "headername : " + headers[i].getName() + ", value: " + headers[i].getValue());
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("json", json));
            nameValuePairs.add(new BasicNameValuePair("orderDetails", orderDetails));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                resultString = EntityUtils.toString(httpEntity).toString();
                Utils.Log(TAG, "response is :" + resultString);
            }
            httpEntity = null;
            response = null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.Log(TAG, "invoke2 Exception : " + e.getLocalizedMessage());
        }
        httppost = null;
        httpclient = null;
        return resultString;
    }

    public static String performGetReQuest(HttpURLConnection myURLConnection) throws Exception {
        myURLConnection.setRequestMethod("GET");
        myURLConnection.connect();
        boolean isError = myURLConnection.getResponseCode() >= 400;
        //The normal input stream doesn't work in error-cases.
        InputStream is = isError ? myURLConnection.getErrorStream() : myURLConnection.getInputStream();
        //InputStream response = myURLConnection.getInputStream();
        String json;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        json = sb.toString();
        Log.e(TAG, "The Response is:::" + json);
        return json;
    }

    public static String performPostCall(HttpURLConnection conn, String postData) throws Exception {
        String response = "";
        Log.e(TAG + "Response", "performPostCall");
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; PPC; en-US; rv:1.3.1)");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream output = conn.getOutputStream();
        output.write(postData.getBytes());
        output.close();
        int responseCode = conn.getResponseCode();
        boolean isError = conn.getResponseCode() >= 400;
        //The normal input stream doesn't work in error-cases.
        InputStream is = isError ? conn.getErrorStream() : conn.getInputStream();
        Log.e(TAG, "responseCode====>" + responseCode);
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            response += line;
        }
        return response;
    }

    public void setResponseHandler(post_sync.ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    protected void onPreExecute() {
        try {
            if (Constants.dialog != null) {
                Constants.dialog = null;
            }
            Constants.dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            progressbar = new ProgressBar(context);
            progressbar.setBackgroundColor(Utils.getColor(context, android.R.color.transparent));
            progressbar.setProgressDrawable(Utils.getDrawable(context, R.drawable.progress_background));
            Constants.dialog.setContentView(progressbar);
            Constants.dialog.setCancelable(false);
            Window window = Constants.dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            Constants.dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 3) {
            invoke2(params[0], params[1], params[2]);
        } else if (params.length == 2) {
            invoke1(params[0], params[1]);
        } else {
            connect_post(params[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("System out", "result: " + resultString);
        if (Constants.dialog != null) {
            Constants.dialog.dismiss();
        }
        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("here in post execute method");
        sendResult();
    }

    private void sendResult() {
        try {
            String str = "";
            String p = "\\\\";
            if (responseHandler != null) {
                if (resultString != null) {
                    str = resultString.replace("\\\\", "\\");
                    if (str.contains("\\\\")) {
                        str = str.replace("\\\\", "\\");
                    }
                }
                responseHandler.onResponse(str, action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect_post(String obj) {
        try {
            String urlStr = obj.replaceAll(" ", "%20").replaceAll("'", "%27");
            Utils.Log(TAG, "connect_post Url==> " + urlStr);
            URL url;
            HttpURLConnection connection;
            StringBuffer buffer = null;
            try {
                url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                buffer = new StringBuffer();
                InputStreamReader inputReader = new InputStreamReader(connection.getInputStream());
                BufferedReader buffReader = new BufferedReader(inputReader, 50000);
                String line = "";
                do {
                    line = buffReader.readLine();
                    if (line != null)
                        buffer.append(line);
                } while (line != null);

                buffReader.reset();
                line = "";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Utils.Log(TAG, "connect_post MalformedURLException " + e.getLocalizedMessage());
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                Utils.Log(TAG, "connect_post OutOfMemoryError " + e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Utils.Log(TAG, "connect_post IOException " + e.getLocalizedMessage());
            }
            resultString = buffer.toString();
            buffer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
