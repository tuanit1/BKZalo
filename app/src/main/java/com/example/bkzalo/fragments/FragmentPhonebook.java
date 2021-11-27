package com.example.bkzalo.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.activitiy.ForgotpassActivity;
import com.example.bkzalo.activitiy.LoginActivity;
import com.example.bkzalo.activitiy.Profile_PB_Activity;
import com.example.bkzalo.adapters.ChatListAdapter;
import com.example.bkzalo.adapters.PhoneBookListAdapter;
import com.example.bkzalo.asynctasks.LoadChatListAsync;
import com.example.bkzalo.asynctasks.LoadPhoneBookListAsync;
import com.example.bkzalo.listeners.ChatListListener;
import com.example.bkzalo.listeners.ClickPhoneBookListener;
import com.example.bkzalo.listeners.PhoneBookListListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.RequestBody;

public class FragmentPhonebook extends Fragment {

    private View view;
    private RecyclerView rv_phonebook;
    private Methods methods;
    private ArrayList<User> arrayList_user;
    private PhoneBookListAdapter adapter;
    private EditText edt_search;
    private ImageView singout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_phonebook, container, false);

        AnhXa();

        methods = new Methods(getContext());

        arrayList_user = new ArrayList<>();

        LoadPhoneBookList();

        return view;
    }

    private void AnhXa() {
        rv_phonebook = view.findViewById(R.id.rv_phonebook);
        edt_search = view.findViewById(R.id.edt_search_phonebook);

    }

    private void Search()
    {
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private boolean tryParseInt(String s)
    {
        try {
            int n = Integer.parseInt(s);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void filter(String s)
    {
        ArrayList<User> filteredList = new ArrayList<>();

        for (User user : arrayList_user)
        {
            if (tryParseInt(s))
            {
                if (user.getPhone().toLowerCase().contains(s.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }
            else {
                if (user.getName().toLowerCase().contains(s.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }

        ClickPhoneBookListener listener = new ClickPhoneBookListener() {
            @Override
            public void onClick(User user) {
                Intent intent = new Intent(getContext(), Profile_PB_Activity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        };

        adapter = new PhoneBookListAdapter(filteredList, listener, getContext());

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_phonebook.setLayoutManager(llm);
        rv_phonebook.setAdapter(adapter);
    }



    private void LoadPhoneBookList() {

        int uid = Constant.UID;

        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);

        RequestBody requestBody = methods.getRequestBody("method_get_phonebook_list", bundle, null);

        PhoneBookListListener listener = new PhoneBookListListener() {
            @Override
            public void onStart() {
                arrayList_user.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<User> arrayList) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_user.addAll(arrayList);
                        SetAdapter();
                        Search();
                    }else {
                        Toast.makeText(getContext(), "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadPhoneBookListAsync loadPhoneBookListAsync = new LoadPhoneBookListAsync(requestBody, listener);
        loadPhoneBookListAsync.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                //reload
                LoadPhoneBookList();
            }
        }
    }

    private void SetAdapter(){

        ClickPhoneBookListener listener = new ClickPhoneBookListener() {
            @Override
            public void onClick(User user) {
                Intent intent = new Intent(getContext(), Profile_PB_Activity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);

                startActivityForResult(intent, 2);
            }
        };

        adapter = new PhoneBookListAdapter(arrayList_user, listener, getContext());

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_phonebook.setLayoutManager(llm);
        rv_phonebook.setAdapter(adapter);

    }

}