package com.example.quanlibanhang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlibanhang.adapter.ProductAdapter;
import com.example.quanlibanhang.dao.ProductDAO;
import com.example.quanlibanhang.model.Products;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    //tìm kiếm
    EditText edTimKiem;
    ImageView imgTimkiem, imgUser, imgLogo;
    Button imgClose;

    //đổi mật khẩu
    EditText edPassOld, edPassNew;
    Button btnOk;

    RecyclerView rvProduct;
    FloatingActionButton floatAdd;

    List listProduct;

    ProductDAO dao;
    private DatabaseReference mDatabase;


    Dialog dialog;
    EditText edTen, edGia, edMota, edAvatar;
    TextView tv_massage;
    Button btnCancel, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // tìm kiếm
        edTimKiem = findViewById(R.id.edFindProduct);
        imgTimkiem = findViewById(R.id.imgTimkiem);
        imgClose = findViewById(R.id.imgClose);
        imgUser = findViewById(R.id.imgUser);
        imgLogo = findViewById(R.id.imgLogo);

        rvProduct = findViewById(R.id.rvProduct);
        floatAdd = findViewById(R.id.fbtAdd);

        listProduct = new ArrayList();
        mDatabase = FirebaseDatabase.getInstance().getReference("products");
        dao = new ProductDAO();
        dao.read(mDatabase, listProduct, this, rvProduct);

        imgLogo.setOnClickListener(view -> {
            logOut(this);
        });

        //tìm kiếm
        imgTimkiem.setOnClickListener(view -> {
            imgTimkiem.setVisibility(View.GONE);
            edTimKiem.setVisibility(View.VISIBLE);
            imgClose.setVisibility(View.VISIBLE);
            {
                edTimKiem.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String strName = edTimKiem.getText().toString().trim();
                        dao.find(strName, mDatabase, listProduct, getApplicationContext(), rvProduct);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

        });

        imgClose.setOnClickListener(view -> {
            edTimKiem.setText("");
            imgTimkiem.setVisibility(View.VISIBLE);
            edTimKiem.setVisibility(View.GONE);
            imgClose.setVisibility(View.GONE);
        });

        //đổi mật khẩu
        imgUser.setOnClickListener(view -> {
            openChangPass(this);
        });

        //khi click vào nút floatButton sẽ mở ra dialog
        floatAdd.setOnClickListener(view -> {
            openDialogAdd(this);
        });

    }

    //Đăng xuất
    protected void logOut(final Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_dangxuat);

        // Khi chạm bên ngoài dialog sẽ ko đóng
        dialog.setCanceledOnTouchOutside(false);

        Button btnYes =dialog. findViewById(R.id.btnYesExit);
        Button btnNo = dialog.findViewById(R.id.btnNoExit);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
                dialog.cancel();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    protected void openDialogAdd(final Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.product_add_dialog);
        //khi chạm ngoài dialog sẽ không đóng
        dialog.setCanceledOnTouchOutside(false);
        edTen = dialog.findViewById(R.id.edTen);
        edGia = dialog.findViewById(R.id.edGia);
        edMota = dialog.findViewById(R.id.edMota);
        edAvatar = dialog.findViewById(R.id.edAvatar);
        tv_massage = dialog.findViewById(R.id.tv_message);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave = dialog.findViewById(R.id.btnSave);

        //khi nhấn btn save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ten = edTen.getText().toString();
                String gia = edGia.getText().toString();
                String mota = edMota.getText().toString();
                String avatar = edAvatar.getText().toString();

                if (TextUtils.isEmpty(ten) || TextUtils.isEmpty(gia)
                        || TextUtils.isEmpty(mota) || TextUtils.isEmpty(avatar)) {
                    tv_massage.setVisibility(View.VISIBLE);
                    tv_massage.setText("Không được để trống");
                } else {
                    dao.add(context, mDatabase, ten, gia, mota, avatar);
                    Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        //khi nhấn btn cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void openChangPass(final Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_changpass);
        //khi chạm bên ngoài dialog sẽ đóng và hủy thay đổi
        dialog.setCanceledOnTouchOutside(true);

        //đổi mật khẩu
        edPassOld = dialog.findViewById(R.id.edPassOld);
        edPassNew = dialog.findViewById(R.id.edPassNew);
        btnOk = dialog.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(view -> {
            String p = edPassOld.getText().toString();
            String p1 = edPassNew.getText().toString();
            if (TextUtils.isEmpty(p) || TextUtils.isEmpty(p1)) {
                Toast.makeText(context, "Vui lòng nhập thông tin", Toast.LENGTH_SHORT).show();
            } else {
                new MainActivity().changPass(context, p, p1);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
        dialog.show();

    }
}