package edu.aku.hassannaqvi.cbt_child_recruitment.syncclasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import edu.aku.hassannaqvi.cbt_child_recruitment.AppMain;
import edu.aku.hassannaqvi.cbt_child_recruitment.CDL;
import edu.aku.hassannaqvi.cbt_child_recruitment.DatabaseHelper;
import edu.aku.hassannaqvi.cbt_child_recruitment.contracts.FormsContract;

/**
 * Created by hassan.naqvi on 7/26/2016.
 */
public class SyncForms extends AsyncTask<Void, Void, String> {

    private static final String TAG = "SyncForms";
    private Context mContext;
    private ProgressDialog pd;


    public SyncForms(Context context) {
        mContext = context;
    }

    public static void longInfo(String str) {
        if (str.length() > 4000) {
            Log.i(TAG, str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.i("TAG: ", str);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setTitle("Please wait... Processing Forms");
        pd.show();
    }


    @Override
    protected String doInBackground(Void... params) {
        try {
            //String url = AppMain.PROJECT_URI + FormsContract.FormsTable.URI;
            String url = AppMain.PROJECT_URI;
            Log.d(TAG, "doInBackground: URL " + url);
            return downloadUrl(url);
        } catch (IOException e) {
            return "Unable to upload data. Server may be down.";
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        String line = "No Response";
        // Only display the first 500 characters of the retrieved
        // web page content.
        //int len = 500;

        //DatabaseHelper db = new DatabaseHelper(mContext);
        //Collection<FormsContract> forms = db.getUnsyncedForms();
        //Log.d(TAG, String.valueOf(forms.size()));

        //if (forms.size() > 0) {

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            // Starts the query
            conn.connect();
            JSONArray jsonSync = new JSONArray();

            //try {

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                /*for (FormsContract fc : forms) {
                    jsonSync.put(fc.toJSONObject());
                    Log.d(TAG, "downloadUrl: " + fc.toJSONObject());
                }*/

            wr.writeBytes(jsonSync.toString().replace("\uFEFF", "") + "\n");
            //longInfo(jsonSync.toString().replace("\uFEFF", "") + "\n");
            wr.flush();

            /*} catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/

            /*===================================================================*/


            File folder = new File(Environment.getExternalStorageDirectory() + "/Download/com/forms/GSED LF-media");
            if (!folder.exists()) {
                Toast.makeText(mContext.getApplicationContext(), "ODK not found in this device install ODK first", Toast.LENGTH_SHORT).show();
                return "";
            }


            int HttpResult = conn.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "utf-8"));
                StringBuffer sb = new StringBuffer();

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println("" + sb.toString());
                return sb.toString();
            } else {
                System.out.println(conn.getResponseMessage());
                return conn.getResponseMessage();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*} else {
            return "No new records to sync";
        }*/

        return line;
        /*===================================================================*/

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        int sSynced = 0;
        String sError = "";
        try {

            if (result.equals("No Response")) {
                Toast.makeText(mContext.getApplicationContext(), "No response from the server", Toast.LENGTH_SHORT).show();
                pd.cancel();
            } else {

                JSONArray json = new JSONArray(result);


                File file = new File(Environment.getExternalStorageDirectory() +
                        "/Download/com/forms/GSED LF-media/mine_enroll_info_csv.csv.imported");

                String csvString = CDL.toString(json);
                FileUtils.writeStringToFile(file, csvString);


                //DatabaseHelper db = new DatabaseHelper(mContext);

            /*for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = new JSONObject(json.getString(i));
                if (jsonObject.getString("status").equals("1")) {
                    sSynced++;
                } else if (jsonObject.getString("error").equals("1")) {
                    sError += "[" + jsonObject.getString("id") + "] " + jsonObject.getString("message") + "\n";
                }
            }*/

                //Toast.makeText(mContext, sSynced + " Forms synced." + String.valueOf(json.length() - sSynced) + " Errors.", Toast.LENGTH_SHORT).show();

                Toast.makeText(mContext, "CSV file downloaded", Toast.LENGTH_LONG).show();

                //pd.setMessage("CSV Downloaded");
                //pd.setTitle("CSV Downloaded");
                //pd.show();
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error CSV downloading " + e.getMessage(), Toast.LENGTH_SHORT).show();

            //pd.setMessage("Error CSV downloading");
            //pd.setTitle("Error CSV downloading");
            //pd.show();
        }
    }
}