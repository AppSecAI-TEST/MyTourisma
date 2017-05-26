package com.ftl.tourisma.postsync;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fipl11111 on 01-Mar-16.
 */
public class post_sync extends AsyncTask<String, Integer, String> {
    static String resultString = "";
    Dialog dialog;
    String activity = "", action = "";
    ProgressBar progressbar;
    private Context context;
    private boolean isShowProgress, isInProgress;
private ResponseHandler responseHandler;

    public post_sync(Context context, String action, ResponseHandler responseHandler, boolean isShowProgress) {
        this.action = action;
        this.context = context;
        this.responseHandler = responseHandler;
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        progressbar = new ProgressBar(context);
        progressbar.setBackgroundColor(Utils.getColor(context, android.R.color.transparent));
        progressbar.setProgressDrawable(Utils.getDrawable(context, R.drawable.progress_background));
        this.isShowProgress = isShowProgress;
    }

    private static String invoke1(String url, String umid) {
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
        Log.i("System out", "link : " + url);
        try {
            org.apache.http.Header[] headers = request.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                Log.i("System out", "headerName : " + headers[i].getName()
                        + ", value: " + headers[i].getValue());
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("json", umid));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                resultString = EntityUtils.toString(httpEntity).toString();
                Log.i("System out", "response is :" + resultString);
            }
            httpEntity = null;
            response = null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        httppost = null;
        httpclient = null;
        return resultString;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (isShowProgress) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.setContentView(progressbar);
                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                dialog.show();
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        invoke1(params[0], params[1]);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (dialog != null) {
            dialog.dismiss();
        }
        sendResult();
    }

    private void sendResult() {
        if (responseHandler != null) {
            String str = "";
            if (responseHandler != null) {
                if (resultString != null) {
                    str = resultString.replace("\\\\", "\\");
                    if (str.contains("\\\\")) {
                        str = str.replace("\\\\", "\\");
                    }
                }
                responseHandler.onResponse(str, action);
            }
        }
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public interface ResponseHandler {
        public void onResponse(String response, String action);
    }
}