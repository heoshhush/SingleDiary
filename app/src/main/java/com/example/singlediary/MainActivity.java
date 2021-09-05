package com.example.singlediary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();


        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment1).commit();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            Fragment selected;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId =  item.getItemId();
                switch(itemId){
                    case R.id.botTab_1:
                        selected = fragment1;
                        break;
                    case R.id.botTab_2:
                        selected = fragment2;
                        break;
                    case R.id.botTab_3:
                        selected = fragment3;
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selected).commit();
                return true;
            }
        });
    }

    @Override
    public void onTabSelected(int position) {
        switch(position){
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.botTab_1);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.botTab_2);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.botTab_3);
                break;
        }
    }
}