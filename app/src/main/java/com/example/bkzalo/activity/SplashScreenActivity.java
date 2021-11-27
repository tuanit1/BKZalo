package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.GetUIDAsync;
import com.example.bkzalo.listeners.GetUIDListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import okhttp3.RequestBody;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView welcome, bkzalo;
    private LottieAnimationView lottie;
    private LinearLayout background;
    private SharedPreferences preferences2;
    private LoginActivity loginActivity = new LoginActivity();
    private FirebaseAuth mAuth;
    private Methods methods;
    private  GoogleSignInOptions gso ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        AnhXa();

        welcome.animate().translationY(-1400).setDuration(2700).setStartDelay(5600);
        bkzalo.animate().translationY(-1400).setDuration(2700).setStartDelay(5600);
        lottie.animate().translationY(1400).setDuration(2700).setStartDelay(5600);

        Open();
    }

    private void AnhXa()
    {
        preferences2 = getSharedPreferences("dataLogin", MODE_PRIVATE);
        methods = new Methods(this);
        mAuth = FirebaseAuth.getInstance();
        welcome = findViewById(R.id.tv_welcome);
        bkzalo = findViewById(R.id.tv_bkzalo);
        lottie = findViewById(R.id.lottie);
        background = findViewById(R.id.linear_background);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1098760652201-es3clrhofv689dge7bpoav34bqt4qsvv.apps.googleusercontent.com")
                .requestEmail()
                .build();

        Constant.mGoogleSignInClient = GoogleSignIn.getClient(SplashScreenActivity.this, gso);
    }

    private void Open()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if( Constant.mGoogleSignInClient!=null)
                {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String email = currentUser.getEmail();
                        GetUIDFB_GG(email, methods);
                }
                else if (AccessToken.getCurrentAccessToken()!=null)
                {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String email = currentUser.getEmail();
                    GetUIDFB_GG(email, methods);
                }
                else
                {
                    String email = preferences2.getString("email", "");
                    String password = preferences2.getString("password", "");
                    boolean isLogin = preferences2.getBoolean("isLogin", false);
                    if (isLogin)
                    {
                        if (email!="" && password!="")
                        {
                            Login (email,password, mAuth, methods, preferences2);
                        }
                    }
                    else
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        }, 6935);
    }


    public void Login(String username, String password, FirebaseAuth mAuth, Methods methods, SharedPreferences preferences2)
    {
        if (Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user.isEmailVerified())
                        {
                            String email = username;
                            DataLogin(email, password, preferences2);
                            GetUID(username, methods);

                        }
                        else
                        {
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                    }
                }
            });

        }
        else
        {
            Query check = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(username);

            check.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        String email = snapshot.child(username).child("email").getValue(String.class);
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.isEmailVerified())
                                    {
                                        DataLogin(email, password, preferences2);
                                        GetUID(email, methods);

                                    }
                                    else
                                    {
                                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                                else
                                {
                                }
                            }
                        });
                    }
                    else
                    {
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void GetUID(String email, Methods methods) {

        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        RequestBody requestBody = methods.getRequestBody("method_get_user", bundle, null);

        GetUIDListener listener = new GetUIDListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, User user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Constant.UID = user.getId();
                        Constant.PHONE = user.getPhone();
                        Constant.NAME = user.getName();
                        Constant.IMAGE = user.getImage();
                    }else {
                        Constant.UID = 0;
                    }

                    if (Constant.IMAGE.equals("null"))
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, Update_InfoActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        Log.e("check", "Open Main: " + Thread.currentThread().getStackTrace()[2].getLineNumber());
                        startActivity(intent);
                        finish();
                    }

                }else{
                }
            }
        };

        GetUIDAsync getUIDAsync = new GetUIDAsync(requestBody, listener);
        getUIDAsync.execute();
    }

    public void GetUIDFB_GG(String email, Methods methods) {

        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        RequestBody requestBody = methods.getRequestBody("method_get_user", bundle, null);

        GetUIDListener listener = new GetUIDListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, User user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Constant.UID = user.getId();
                        Constant.PHONE = user.getPhone();
                        Constant.NAME = user.getName();
                        Constant.IMAGE = user.getImage();
                    }else {
                        Constant.UID = 0;
                    }

                        if (Constant.UID==0)
                        {
                            Intent intent = new Intent(SplashScreenActivity.this,CreatePF_Activity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            if(fuser != null){
                                if (Constant.IMAGE.equals("null"))
                                {
                                    Intent intent = new Intent(SplashScreenActivity.this, Update_InfoActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                    Log.e("check", "Open Main: " + Thread.currentThread().getStackTrace()[2].getLineNumber());
                                    startActivity(intent);
                                }

                            }
                        }

                }else{
                }
            }
        };

        GetUIDAsync getUIDAsync = new GetUIDAsync(requestBody, listener);
        getUIDAsync.execute();
    }

    private void DataLogin (String email, String password, SharedPreferences preferences2)
    {
        SharedPreferences.Editor editor = preferences2.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("isLogin", true);
        editor.commit();
    }


}