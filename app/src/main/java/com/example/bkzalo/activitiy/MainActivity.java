package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync;
import com.example.bkzalo.fragments.FragmentMessage;
import com.example.bkzalo.fragments.FragmentPhonebook;
import com.example.bkzalo.fragments.FragmentProfile;
import com.example.bkzalo.fragments.FragmentSetting;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction fragmentTransaction;
    private Methods methods;
    private Socket socket;

    private static final int FRAGMENT_MESSAGE = 1;
    private static final int FRAGMENT_PHONEBOOK = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_SETTING = 4;

    private int currentFragment = FRAGMENT_MESSAGE;


    @Override
    protected void onDestroy() {
        Log.e("check", "main destroy");
        UpdateOnline(false);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("check", "main start");

        Constant.verifyStoragePermissions(this);

        methods = new Methods(this);

        UpdateOnline(true);

        AnhXa();
        InitSocketIO();

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

    private void InitSocketIO(){
        try {
            socket = IO.socket(Constant.SERVER_NODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        socket.connect();
    }

    private void UpdateOnline(Boolean mStatus){

        Bundle bundle = new Bundle();
        bundle.putInt("user_id", Constant.UID);
        bundle.putInt("status", mStatus?1:0);

        RequestBody requestBody = methods.getRequestBody("method_update_online", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Bundle bundle = new Bundle();

                        bundle.putString("user_id", String.valueOf(Constant.UID));
                        bundle.putString("status", mStatus?"1":"0");

                        String json = new Gson().toJson(bundle);

                        socket.emit("update online", json);
                    }else {
                        Toast.makeText(MainActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();


    }

}