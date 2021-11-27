package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync_Nhi;
import com.example.bkzalo.listeners.ExecuteQueryListener_Nhi;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.RequestBody;

public class Update_InfoActivity extends AppCompatActivity {

    private TextView tv_birthday;
    private DatePickerDialog.OnDateSetListener setListener = null;
    private Calendar calendar = Calendar.getInstance();
    final int ngay = calendar.get(Calendar.DATE);
    final int thang = calendar.get(Calendar.MONTH);
    final int nam = calendar.get(Calendar.YEAR);
    private String birthday1, image, birthday2;
    private Integer uid;
    private Methods methods;
    private Button btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        AnhXa();
    }

    private void AnhXa()
    {
        methods = new Methods(this);

        uid = Constant.UID;

        tv_birthday = findViewById(R.id.tv_update_bday);

        tv_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Update_InfoActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener, nam, thang, ngay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int i, int i1, int i2) {
                //i: năm - i1: tháng - i2: ngày
                calendar.set(i,i1,i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                tv_birthday.setText(simpleDateFormat.format(calendar.getTime()));

                birthday1 = tv_birthday.getText().toString().trim();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(birthday1);
                    birthday2= sdf.format(date);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        btn_update = findViewById(R.id.next_updateinfo);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Update();
            }
        });
    }

    private void Update()
    {
        Bundle bundle = new Bundle();

        bundle.putInt("uid", uid);
        bundle.putString("birthday", birthday2);
        bundle.putString("image", image);


        RequestBody requestBody = methods.getRequestBody("method_update_info", bundle, null);

        ExecuteQueryListener_Nhi listener = new ExecuteQueryListener_Nhi() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Intent intent = new Intent(Update_InfoActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(Update_InfoActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Update_InfoActivity.this, "Chưa kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync_Nhi async = new ExecuteQueryAsync_Nhi(requestBody, listener);
        async.execute();
    }



}