package mobi.wonders.apps.android.netdiagnose;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import mobi.wonders.apps.android.netdiagnose.net.NetDiagnoseload;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_website;
    private String website;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        et_website = (EditText) findViewById(R.id.et_website);
        findViewById(R.id.test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        website = et_website.getText().toString().trim();
        Log.d("onClick", website);
        new NetDiagnoseload(this, website);
    }

}
