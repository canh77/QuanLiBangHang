package com.example.quanlibanhang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText edEmail1,edPassword1;
    Button btnLogin,btnTTK, btnDangKy;
    TextView tvDangNhap;
    CheckBox chkRememberPass;
    //tạo biến kiểm tra đúng sai
    int check;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        tvDangNhap = findViewById(R.id.tvDangNhap);
        edEmail1= findViewById(R.id.edEmail1);
        edPassword1 = findViewById(R.id.edPassword1);
        chkRememberPass = findViewById(R.id.chkRememberPass);

        // lưu u và p vào SharedPreferences
        // lưu rồi thì đọc ra đọc user, pass
        // còn chưa lưu thì gán u và p bằng rỗng
        SharedPreferences pref = getSharedPreferences("USER_FILE",MODE_PRIVATE);
        String email =pref.getString("EMAIL","");
        String pass= pref.getString("PASSWORD","");
        Boolean rem=  pref.getBoolean("REMEMBER",false);
        //là dữ liệu có sẵn mà khi chúng ta vừa nhập ở trên SharedPreferences
        edEmail1.setText(email);
        edPassword1.setText(pass);
        chkRememberPass.setChecked(rem);

        btnLogin = findViewById(R.id.btnLogin);
        btnTTK = findViewById(R.id.btnTTK);
        btnDangKy = findViewById(R.id.btnDangKy);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edEmail1.getText().toString().trim();
                String strPassword = edPassword1.getText().toString().trim();
                if (kiemtra(strEmail,strPassword) == 1){
                    dangNhap(strEmail,strPassword);
                }
            }
        });

        btnTTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDangNhap.setText("ĐĂNG KÝ TÀI KHOẢN");
                chkRememberPass.setVisibility(View.GONE);
                btnTTK.setVisibility(View.GONE);
                btnDangKy.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edEmail1.getText().toString().trim();
                String strPassword = edPassword1.getText().toString().trim();
                if (kiemtra(strEmail,strPassword) == 1){
                    dangKi(strEmail,strPassword);
                }
            }
        });
    }

    private void  dangNhap(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                //thông báo khi thành công
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Đăng nhập thàng công", Toast.LENGTH_SHORT).show();
                            rememberUser(email,password,chkRememberPass.isChecked());
                            startActivity(new Intent(getApplicationContext(),ProductActivity.class));
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //ghi nhớ pass và user vào ô checkbox
    public  void rememberUser(String e,String p,boolean status){//status là  đúng sai
        SharedPreferences pref =getSharedPreferences("USER_FILE",MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();//edit là cho phép sửa
        if(!status){//nếu user và pass sai thì clear form
            //phủ định thì xóa tình trạng lưu trước đó
            edit.clear();
        }else {
            //lưu data vào file
            edit.putString("EMAIL",e);
            edit.putString("PASSWORD",p);
            //status là lưu khi đúng định dạng
            edit.putBoolean("REMEMBER",status);
        }
        //lưu lại all data
        edit.commit();
    }


    private void  dangKi(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                //thông báo khi thành công
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Đăng kí thàng công", Toast.LENGTH_SHORT).show();

                            tvDangNhap.setText("ĐĂNG NHẬP HỆ THỐNG");
                            chkRememberPass.setVisibility(View.VISIBLE);
                            btnTTK.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            btnDangKy.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(MainActivity.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void changPass(final Context context, String pass_old, final String new_pass){
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser(); // lấy user hiện tại
        final String email = user.getEmail(); // lấy email của user
        AuthCredential credential = EmailAuthProvider.getCredential(email, pass_old); // lấy về credential
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            user.updatePassword(new_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Log.e("//==Err", "Đổi mật khẩu thất bại");
                        }
                    }
                });
    }


    private  int kiemtra(String email,String password){
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
            check = -1;
        }else {
            if (password.length() < 6){
                Toast.makeText(this, "Mật khẩu phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                check = -1;
            }else
                check = 1;
        }
        return check;
    }
}

