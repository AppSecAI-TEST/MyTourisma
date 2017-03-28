package com.ftl.tourisma.utils;

/**
 * Created by fipl11111 on 28-Oct-15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class CommonClass {

    /**
     * Web Service Api List.
     */
    public final static String UserName = null;
    public static String fb_id_s = null;

    //=========================================================
    // email pattern
    private static Pattern email_pattern = Pattern
            .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

    public static boolean hasInternetConnection(final Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    /*public static void getAlert(Context context,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error").setMessage(msg).setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        TextView mg = (TextView) alert.findViewById(android.R.id.message);
        mg.setGravity(Gravity.CENTER);
    }*/
    public static boolean checkEmail(String uemail) {
        return email_pattern.matcher(uemail).matches();
    }

    public static String convertInputStreamToString(InputStream is) {
        BufferedReader reader;
        StringBuilder sb = null;
        try {
//			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8); // JSON is UTF-8 by default I believe
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            sb = new StringBuilder();
            String line = null;
//			returnResponse = reader.readLine();
//			Log.d("DEBUG", "returnResponse: " + returnResponse); // DEBUG
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
//			    Log.d("DEBUG", "sb: " + sb.toString()); // DEBUG
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

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

//		final int color = 0xffff0000;
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 10);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


	/*try{
        Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.putExtra(Intent.EXTRA_TEXT, message);
	    intent.setType("text/plain");
	    final PackageManager pm = context.getPackageManager();
	    final List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);

	    for (int i = 0; i < activityList.size(); i++) {
	        final ResolveInfo app = activityList.get(i);
	        if ((app.activityInfo.name.contains("twitter"))){
	       //if ("com.twitter.android".equals(app.activityInfo.name)) {
	            final ActivityInfo activity = app.activityInfo;
	            final ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
	            intent=new Intent(Intent.ACTION_SEND);
	            intent.addCategory(Intent.CATEGORY_LAUNCHER);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	            intent.setComponent(name);
	            intent.putExtra(Intent.EXTRA_TEXT, message);
	            context.startActivity(intent);
	            break;
	        }

	    //}

	  }

	}
	catch(final ActivityNotFoundException e) {
	    Log.i("mufumbo", "no twitter native",e );

	}*/
}
