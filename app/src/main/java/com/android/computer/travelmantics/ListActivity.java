package com.android.computer.travelmantics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Enabling Offline Persistence
        FirebaseUtil.getDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        } else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                startActivity(new Intent(ListActivity.this, DealActivity.class));
                return true;
            case R.id.logout_menu:
                FirebaseUtil.signOut();
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
        showMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFirebaseRef("travelDeals", this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final TravelDealAdapter adapter = new TravelDealAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseUtil.attachListener();
        if (FirebaseUtil.mFirebaseAuth.getUid() != null) {
            FirebaseUtil.checkAdmin(FirebaseUtil.mFirebaseAuth.getUid());
            showMenu();
        }
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }
}
