package com.example.nasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class Favorites extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static final String testToolbar = "Toolbar Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_favorites);

        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.fav);
        Log.d(testToolbar,"User started the toolbar activity");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open_drawer, R.string.close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                startActivity(new Intent(Favorites.this, MainActivity.class));
                Log.d(testToolbar, "home Item selected");
                break;

            case R.id.search:
                startActivity(new Intent(Favorites.this, Search.class));
                Log.d(testToolbar, "search Item selected");
                break;

            case R.id.fav:

                Log.d(testToolbar, "favorites Item selected");
                break;


        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu items for use in the action bar
        Log.d(testToolbar,"User started the toolbar activity");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.the_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.fav_help ))
                .setPositiveButton(getString(R.string.help_ok), (d, which) -> {
                })
                .create();

        dialog.show();
        return true;
    }
}