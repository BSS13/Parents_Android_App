package com.example.hp.parents;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class launcher extends AppCompatActivity {
    String id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Intent in1 = getIntent();

        if(in1!=null)
        {
            id = in1.getStringExtra("MyCode");
            Log.d("MYMSG",id+" in Home");

        }
        Thread t=new Thread(new myjob());
        t.start();

    }

    class myjob implements Runnable {
        @Override
        public void run() {

            try
            {
                Thread.sleep(4000);
                Intent in=new Intent(launcher.this,MainActivity.class);

                in.putExtra("id",id);
                startActivity(in);

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }


        }

    }
}


