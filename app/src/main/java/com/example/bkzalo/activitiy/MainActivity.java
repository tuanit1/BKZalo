package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bkzalo.R;
import com.example.bkzalo.fragments.FragmentMessage;
import com.example.bkzalo.fragments.FragmentPhonebook;
import com.example.bkzalo.fragments.FragmentProfile;
import com.example.bkzalo.fragments.FragmentSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction fragmentTransaction;

    private static final int FRAGMENT_MESSAGE = 1;
    private static final int FRAGMENT_PHONEBOOK = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_SETTING = 4;

    private int currentFragment = FRAGMENT_MESSAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();

        ReplaceFragment(new FragmentMessage(), "Message");
    }

    private void AnhXa(){
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.bottom_nav_tinnhan:
                        if(currentFragment != FRAGMENT_MESSAGE){
                            ReplaceFragment(new FragmentMessage(), "Message");
                            currentFragment = FRAGMENT_MESSAGE;
                        }
                        break;
                    case R.id.bottom_nav_danhba:
                        if(currentFragment != FRAGMENT_PHONEBOOK){
                            ReplaceFragment(new FragmentPhonebook(), "Phonebook");
                            currentFragment = FRAGMENT_PHONEBOOK;
                        }
                        break;
                    case R.id.bottom_nav_canhan:
                        if(currentFragment != FRAGMENT_PROFILE){
                            ReplaceFragment(new FragmentProfile(), "Profile");
                            currentFragment = FRAGMENT_PROFILE;
                        }
                        break;
                    case R.id.bottom_nav_caidat:
                        if(currentFragment != FRAGMENT_SETTING){
                            ReplaceFragment(new FragmentSetting(), "Setting");
                            currentFragment = FRAGMENT_SETTING;
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void ReplaceFragment(Fragment fragment, String name){

        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backstackCount; i++){
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();

            getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment, name);
        fragmentTransaction.commit();
    }
}