package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.GetUIDAsync;
import com.example.bkzalo.listeners.GetUIDListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private TextView tv_forgot, tv_signup;
    private EditText edt_username, edt_password;
    private FirebaseAuth mAuth;
    private ImageView ic_google;
    private CheckBox cb_remember;
    private LoginButton ic_facebook;
    private Methods methods;
    private SharedPreferences preferences1, preferences2;
    private CallbackManager mCallbackManager;
    private AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        AnhXa();

        methods = new Methods(this);

        preferences2 = getSharedPreferences("dataLogin", MODE_PRIVATE);
        preferences1 = getSharedPreferences("rememberLogin", MODE_PRIVATE);

        GhiNhoMK();

        accessToken = AccessToken.getCurrentAccessToken();

        Access();


    }


    private void AnhXa(){

        mCallbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();

        cb_remember = findViewById(R.id.cb_remember);

        ic_google = findViewById(R.id.ic_google);
        ic_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleLogin();
            }
        });

        ic_facebook = findViewById(R.id.ic_facebook);
        ic_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookLogin();
            }
        });


        edt_username = findViewById(R.id.edt_username_login);
        edt_password = findViewById(R.id.edt_password_login);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_username.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                if (validateField(username, password))
                {
                    Login(username, password, mAuth, methods, preferences2);
                }
                else
                {
                    return;
                }
            }
        });

        tv_forgot = findViewById(R.id.tv_forgot_login);
        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotpassActivity.class);
                startActivity(intent);
        }
        });

        tv_signup = findViewById(R.id.tv_signup_login);
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1098760652201-es3clrhofv689dge7bpoav34bqt4qsvv.apps.googleusercontent.com")
                .requestEmail()
                .build();

        Constant.mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

    }

    private void FacebookLogin()
    {
        ic_facebook.setLoginBehavior(LoginBehavior.WEB_ONLY);
        ic_facebook.setReadPermissions("public_profile", "email");
        ic_facebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            GetUIDGG_FB(email, methods);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void GoogleLogin()
    {
        try
        {
            Intent intent = Constant.mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode!=RESULT_CANCELED)
        {
            if (requestCode==100)
            {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                    firebaseAuthWithGoogleAccount(account);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            mCallbackManager.onActivityResult(requestCode,resultCode,data);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String email = account.getEmail();
                            GetUIDGG_FB(email, methods);

                        } else {
                            task.getException();
                        }
                    }
    });

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

                            if (cb_remember!=null)
                            {
                                Check_CheckBox(username, password);
                            }

                            String email = username;
                            DataLogin(email, password, preferences2);
                            GetUID(username, methods);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Hãy xác thực email của bạn", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        task.getException();
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
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
                        edt_username.setError(null);
                        String email = snapshot.child(username).child("email").getValue(String.class);
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.isEmailVerified())
                                    {
                                        if (cb_remember!=null)
                                        {
                                            Check_CheckBox(username, password);
                                        }

                                        DataLogin(email, password, preferences2);
                                        GetUID(email, methods);

                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, "Hãy xác thực email của bạn", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    task.getException();
                                    Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_LONG).show();
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

                    if (Constant.IMAGE.isEmpty())
                    {
                        Intent intent = new Intent(LoginActivity.this, Update_InfoActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Log.e("check", "Open Main: " + Thread.currentThread().getStackTrace()[2].getLineNumber());
                        startActivity(intent);
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "no internet", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetUIDAsync getUIDAsync = new GetUIDAsync(requestBody, listener);
        getUIDAsync.execute();
    }


    public void GetUIDGG_FB(String email, Methods methods) {

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
                        Intent intent = new Intent(LoginActivity.this,CreatePF_Activity.class);
                        startActivity(intent);
                    }
                    else {
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            if (fuser!=null)
                            {
                                if (Constant.IMAGE.isEmpty())
                                {
                                    Intent intent = new Intent(LoginActivity.this, Update_InfoActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    Log.e("check", "Open Main: " + Thread.currentThread().getStackTrace()[2].getLineNumber());
                                    startActivity(intent);
                                }
                            }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "no internet", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetUIDAsync getUIDAsync = new GetUIDAsync(requestBody, listener);
        getUIDAsync.execute();
    }
    private boolean validateField(String username, String password)
    {
        if (username.isEmpty())
        {
            edt_username.setError("Không được trống");
            edt_username.requestFocus();
            return false;
        }
        if (password.isEmpty())
        {
            edt_password.setError("Không được trống");
            edt_password.requestFocus();
            return false;
        }

        if (password.isEmpty() && username.isEmpty())
        {
            edt_password.setError("Không được trống");
            edt_username.setError("Không được trống");
            return false;
        }

        return true;
    }

    private void Check_CheckBox(String username, String password)
    {
        if (cb_remember.isChecked())
        {
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putBoolean("checked", true);
            editor.commit();
        }
    }

    private void GhiNhoMK()
    {
        edt_username.setText(preferences1.getString("username", ""));
        edt_password.setText(preferences1.getString("password", ""));
        cb_remember.setChecked(preferences1.getBoolean("checked", false));
    }


    private void DataLogin (String email, String password, SharedPreferences preferences2)
    {
        SharedPreferences.Editor editor = preferences2.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("isLogin", true);
        editor.commit();
    }

    private void Access()
    {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}