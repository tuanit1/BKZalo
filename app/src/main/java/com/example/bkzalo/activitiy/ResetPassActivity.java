package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPassActivity extends AppCompatActivity {

    private Button btn_done;
    private ImageView imv_back;
    private EditText edt_pw1, edt_pw2, edt_pwold;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String pw1, pw2, pw_old;
    private SharedPreferences preferences2, preferences1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        preferences2 = getSharedPreferences("dataLogin", MODE_PRIVATE);
        preferences1 = getSharedPreferences("rememberLogin", MODE_PRIVATE);

        AnhXa();
    }
    private void AnhXa() {

        edt_pw1 = findViewById(R.id.edt_pw1_reset);
        edt_pw2 = findViewById(R.id.edt_pw2_reset);
        edt_pwold = findViewById(R.id.edt_pw_old);

        btn_done = findViewById(R.id.btn_done_reset);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setValidate())
                {
                    ChangePass();
                }
            }
        });

        imv_back = findViewById(R.id.imv_back_reset);
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void ChangePass()
    {
        user.updatePassword(pw1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        boolean isSuccess = task.isSuccessful();

                        if (isSuccess)
                        {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users").child(Constant.PHONE);
                            reference.child("password").setValue(pw1);

                            SharedPreferences.Editor editor2 = preferences2.edit();
                            editor2.putString("password", "");
                            editor2.putBoolean("isLogin", false);
                            editor2.commit();

                            SharedPreferences.Editor editor1 = preferences1.edit();
                            editor1.putString("password", "");
                            editor1.commit();

                            Toast.makeText(ResetPassActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                            if(user != null)
                            {
                                if (Constant.mGoogleSignInClient!=null)
                                {
                                    Constant.mGoogleSignInClient.signOut();
                                }

                                mAuth.signOut();
                            }
                            Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
        });
    }

    private boolean setValidate()
    {
        pw1 = edt_pw1.getText().toString().trim();
        pw2 = edt_pw2.getText().toString().trim();
        pw_old = edt_pwold.getText().toString().trim();

        if (pw1.length() < 6) {
            edt_pw1.setError("Mật khẩu phải có trên 6 kí tự");
            return false;

        }
        if (pw2.length() < 6) {
            edt_pw2.setError("Mật khẩu phải có trên 6 kí tự");
            return false;

        }

        if (!pw1.equals(pw2))
        {
            edt_pw2.setError("Mật khẩu không trùng khớp");
            return false;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(Constant.PHONE);
        String nowpass = reference.child("password").toString();
        if (!pw_old.equals(nowpass))
        {
            edt_pwold.setError("Mật khẩu cũ không đúng");
            return false;
        }

        return true;
    }

}

