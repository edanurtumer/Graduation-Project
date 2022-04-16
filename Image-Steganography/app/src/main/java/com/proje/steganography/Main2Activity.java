package com.proje.steganography;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.ayush.steganography.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class Main2Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("StegaKript");

        if(mAuth.getCurrentUser() != null){

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }
    
    public void onStart() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            
            sendToStart();
            
        }else {

            mUserRef.child("online").setValue("true");

        }

        super.onStart();
    }

    protected void onStop() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

        super.onStop();

    }

    private void sendToStart() {
        Intent startIntent = new Intent(Main2Activity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.main_settings_btn) {

            Intent settingsIntent = new Intent(Main2Activity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_steganografi) {

            Intent steganografiIntent = new Intent(Main2Activity.this, Encode.class);
            startActivity(steganografiIntent);

        }

        if(item.getItemId() == R.id.main_steganaliz) {

            Intent steganalizIntent = new Intent(Main2Activity.this, Decode.class);
            startActivity(steganalizIntent);

        }

        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(Main2Activity.this, UsersActivity.class);
            startActivity(settingsIntent);

        }

        return true;

    }

}
