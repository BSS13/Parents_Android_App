package com.example.hp.parents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class ParentsHomeActivity extends AppCompatActivity {

    TextView tv1;
    Toolbar toolbar;
    ViewPager mViewPager;



    SectionsPagerAdapter mSectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_home);

        tv1=(TextView)(findViewById(R.id.tv1));


        SharedPreferences pref=getSharedPreferences("Parentslogin.txt",MODE_PRIVATE);

        String rollnumber=pref.getString("student_roll",null);

        GlobalData.rollnumber=rollnumber;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        toolbar=(Toolbar)(findViewById(R.id.toolbar));

        toolbar.setTitle("");

        //This will replace toolbar with actionbar
        setSupportActionBar(toolbar);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Child"));
        tabs.addTab(tabs.newTab().setText("Driver"));
        tabs.addTab(tabs.newTab().setText("Entry/Exit"));
        tabs.addTab(tabs.newTab().setText("Messages"));

        tabs.setupWithViewPager(mViewPager);

        tabs.getTabAt(0).setIcon(R.drawable.ic_face_black_24dp);
        tabs.getTabAt(1).setIcon(R.drawable.ic_person_black_24dp);
        tabs.getTabAt(2).setIcon(R.drawable.ic_compare_arrows_black_24dp);
        tabs.getTabAt(3).setIcon(R.drawable.ic_add_alert_black_24dp);


        Intent in = getIntent();
        if(in!=null)
        {
            String id = in.getStringExtra("id");
            Log.d("MYMSG",id +" in Parents Home");
            if(id!=null && id.equals("1"))
            {
                mViewPager.setCurrentItem(3, true);

            }
        }
        //now we can set title of toolbar like we do of actionbar
        //getSupportActionBar().setTitle("Welcome "+GlobalData.drivername);

        tv1.setText("Welcome: PARENTS");
        // OR Some Phones
        // toolbar.setTitle("Hello from TOOLBAR");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuparents, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.changepass) {
            Intent in=new Intent(this,ChangePasswordParents.class);
            startActivity(in);
        }

        if (id == R.id.livetrack) {
            Intent in=new Intent(this,LiveTrackingDriver.class);
            startActivity(in);
        }

        if (id == R.id.trackhistory) {
            Intent in=new Intent(this,TrackingDriverHistory.class);
            startActivity(in);
        }


        if (id == R.id.logout) {

            SharedPreferences sharedPreferences=getSharedPreferences("Parentslogin.txt",MODE_PRIVATE);
            sharedPreferences.edit().remove("student_roll").commit();

            finish();
            Intent innew=new Intent(ParentsHomeActivity.this,MainActivity.class);
            startActivity(innew);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if(position==0)
                return  (new student_info());
            else if(position==1)
                return (new driver_info());
            else if(position==2)
                return (new bus_entry_exit_record());
            else if(position==3)
                return (new breakdown_messages());


            return null;
        }

        @Override
        public int getCount() {

            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Child";
                case 1:
                    return "Driver";
                case 2:
                    return "In/Out";
                case 3:
                    return "Alerts";

            }
            return null;
        }
    }

}



