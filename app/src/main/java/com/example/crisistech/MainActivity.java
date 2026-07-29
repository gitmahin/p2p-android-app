package com.example.crisistech;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mainDrawableMenu;
    Button mainMenuOpenButton;
    Button wifiEnableButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        mainDrawableMenu = findViewById(R.id.mainMenuDrawerLayout);
        mainMenuOpenButton =  findViewById(R.id.wifiEnableButton);
        wifiEnableButton = findViewById(R.id.wifiEnableButton);

        this.init();
    }

    public void init() {
//        mainMenuOpenButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainDrawableMenu.open();
//            }
//        });

        wifiEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable enabledBg = ContextCompat.getDrawable(MainActivity.this, R.drawable.wifi_enable_button);
                Drawable disabledBg = ContextCompat.getDrawable(MainActivity.this, R.drawable.wifi_button);
                if(wifiEnableButton.getText().toString().toLowerCase().equals("wifi on")) {
                wifiEnableButton.setText("Wifi Off");
                wifiEnableButton.setBackground(disabledBg);
                } else {
                    wifiEnableButton.setText("Wifi On");
                    wifiEnableButton.setBackground(enabledBg);
                }
            }
        });
    }
}