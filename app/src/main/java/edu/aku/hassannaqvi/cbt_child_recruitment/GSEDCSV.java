package edu.aku.hassannaqvi.cbt_child_recruitment;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import edu.aku.hassannaqvi.cbt_child_recruitment.syncclasses.SyncForms;

public class GSEDCSV extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsedcsv);
    }

    public void DownloadDataCSV(View view) {
        Toast.makeText(getApplicationContext(), "Syncing Forms", Toast.LENGTH_SHORT).show();
        new SyncForms(this).execute();
    }
}