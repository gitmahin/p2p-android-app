package com.example.crisistech;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mainDrawableMenu;
    Button mainMenuOpenButton;

    NavigationView mainNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainDrawableMenu = findViewById(R.id.mainMenuDrawerLayout);
        mainMenuOpenButton = findViewById(R.id.menu_open_button);
        mainNavigationView = findViewById(R.id.main_navigation_view);

        this.mainMenuManager();
        this.mainNavigationViewManager();
    }

    private void mainMenuManager() {
        mainMenuOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawableMenu.open();
            }
        });


    }


    private void mainNavigationViewManager() {

        mainNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment fragment = new HomeFragment();
                ;

                if (itemId == R.id.nav_home) {
                    fragment = new HomeFragment();
                }

                if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }

                if (itemId == R.id.nav_active_devices) {
                    Toast.makeText(MainActivity.this, "Active Devices", Toast.LENGTH_SHORT).show();
                }

                if (itemId == R.id.nav_terms_conditions) {
                    Toast.makeText(MainActivity.this, "Terms & Conditions", Toast.LENGTH_SHORT).show();
                }

                if (itemId == R.id.nav_privacy_policy) {
                    Toast.makeText(MainActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
                }


                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();


                mainDrawableMenu.close();
                return true;
            }
        });
    }
}