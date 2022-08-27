package com.example.quanlibanhang.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlibanhang.adapter.ProductAdapter;
import com.example.quanlibanhang.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

//Cotroller xử lí
public class ProductDAO {
    String msg = " ";


    //add dữ liệu vào
    public void add(final  Context context,DatabaseReference mDatabase,
                    String name, String price,String mota, String avatar) {
        // tạo key fbase
        String id = mDatabase.push().getKey();
        // tạo đối tượng truyền
        Products p = new Products(id, name, price, mota, avatar);
        // truyền value theo key
        mDatabase.child(id).setValue(p)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //update dữ liệu
    public void update( final  Context context,DatabaseReference mDatabase,String id, String name, String price,
                         String mota, String avatar) {
        mDatabase.child(id).setValue(new Products(id, name, price, mota, avatar))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //xóa dữ liệu
    public void delete(final  Context context,DatabaseReference mDatabase,String id) {
        mDatabase.child(id).setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
//        Log.d("//==Add")
    }

    //đọc dữ liệu ra
    public void read (DatabaseReference mDatabase, List listProduct
    ,Context context, RecyclerView rvProduct) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    listProduct.add(data.getValue(Products.class));
                }
                RecyclerView.LayoutManager manager = new GridLayoutManager(context, 2);
                rvProduct.setLayoutManager(manager);
                ProductAdapter adapter = new ProductAdapter(listProduct, context);
                // hiển thị dữ liệu từ adapter lên RecycleView
                rvProduct.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //tìm sản phẩm
    public void find(String findName,DatabaseReference mDatabase, List listProduct
            ,Context context, RecyclerView rvProduct){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    //chuyển đổi
                    String strData = data.getValue(Products.class).getName().toUpperCase();
                    String strName = findName.toUpperCase();

                    if (strData.contains(strName)) {
                        listProduct.add(data.getValue(Products.class));
                    }

                    //thiết lập lại giao diện sau khi tìm kiếm
                    RecyclerView.LayoutManager manager = new GridLayoutManager(context, 2);
                    rvProduct.setLayoutManager(manager);
                    ProductAdapter adapter = new ProductAdapter(listProduct, context);
                    // hiển thị dữ liệu từ adapter lên RecycleView
                    rvProduct.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
