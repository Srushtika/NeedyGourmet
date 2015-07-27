package com.srushtika.needygourmet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Venks on 1/10/2015.
 */
public class MyMainActivity extends Activity{


    private Button report;
    private Button donate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_alter_main);

        this.report = (Button)findViewById(R.id.bReport);
        this.donate = (Button)findViewById(R.id.bDonate);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openMain = new Intent("com.srushtika.needygourmet.MainActivity");
                startActivity(openMain);
            }
        });

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openMain = new Intent("com.srushtika.needygourmet.ConnectActivity");
                startActivity(openMain);
            }
        });


    }
}
