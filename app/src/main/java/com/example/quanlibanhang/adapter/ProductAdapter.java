package com.example.quanlibanhang.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlibanhang.R;
import com.example.quanlibanhang.dao.ProductDAO;
import com.example.quanlibanhang.model.Products;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
//HIỂN THỊ ITEM THÊM CHI TIẾT VÀ XÓA
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductVH>  {
    List<Products> list;
    Context context;
    //thao tác lệnh và dữ liệu
     ProductDAO dao;
     private DatabaseReference mDatabase;

    String strId, strName, strPrice, strMota, strAvatar;

    public ProductAdapter(List<Products> list,Context context){
        this.list = list;
        this.context = context;

        dao = new ProductDAO();
        mDatabase = FirebaseDatabase.getInstance().getReference("products");
    }

    @NonNull
    @Override
    public ProductAdapter.ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductAdapter.ProductVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductVH holder, int position) {
        Products products = list.get(position);

        //nếu ko có dữ liệu
        if (products == null)  return;

        //nếu có dữ liệu
        holder.ten.setText(products.getName());
        holder.gia.setText(products.getPrice());

        //load ảnh
        Picasso.get().load(products.getAvatar()).into(holder.imgArt);

        //hiển thị chi tiết khi click vào item
        holder.detail.setOnClickListener(view -> {
            {
                strId = products.getId();
                strName = products.getName();
                strPrice = products.getPrice();
                strMota = products.getMota();
                strAvatar = products.getAvatar();
                Log.d("//==Detail", strName + " | " + strPrice);
            }

            //hiển thị dialog chi tiết và 2 nút cập nhật và xóa
            {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.product_detail_dialog);

                dialog.setCanceledOnTouchOutside(true);

                TextView tvTen, tvGia, tvMoTa;
                ImageView imgDetail;
                Button btnDelete, btnUpdate;
                imgDetail = dialog.findViewById(R.id.ivImg);
                tvTen = dialog.findViewById(R.id.tvTen);
                tvGia = dialog.findViewById(R.id.tvGia);
                tvMoTa = dialog.findViewById(R.id.tvMota);
                btnDelete = dialog.findViewById(R.id.btnDelete);
                btnUpdate = dialog.findViewById(R.id.btnUpdate);

                // Hiển thị dữ liệu lên DialogDetail
                {
                    tvTen.setText(strName);
                    tvGia.setText(strPrice);
                    tvMoTa.setText(strMota);
                    Picasso.get().load(strAvatar).into(imgDetail);

                }
                // Cập nhật và xóa
                {
                    btnUpdate.setOnClickListener(view1 -> {
                        dialog.dismiss();
                        openDialogUpdate(view1.getContext());
                    });

                    btnDelete.setOnClickListener(view1 -> {
                        dialog.dismiss();
                        openDialogDelete(view1.getContext());
                    });
                }
                dialog.show();
            }
            });
    }
    private  void  openDialogUpdate(final  Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.product_add_dialog);
        // Khi chạm bên ngoài dialog sẽ ko đóng
        dialog.setCanceledOnTouchOutside(false);
        {
            EditText edTen, edGia, edMota, edAvatar;
            TextView tv_message ,tvTilte;
            Button btnCancel, btnSave;
            tvTilte = dialog.findViewById(R.id.tvTitle);
            edTen = dialog.findViewById(R.id.edTen);
            edGia = dialog.findViewById(R.id.edGia);
            edMota = dialog.findViewById(R.id.edMota);
            edAvatar = dialog.findViewById(R.id.edAvatar);
            tv_message = dialog.findViewById(R.id.tv_message);
            btnCancel = dialog.findViewById(R.id.btnCancel);
            btnSave = dialog.findViewById(R.id.btnSave);
            //hiển thị dữ liệu lên khi update xong
            {
                tvTilte.setText("Cập nhật sản phẩm");
                edTen.setText(strName);
                edGia.setText(strPrice);
                edMota.setText(strMota);
                edAvatar.setText(strAvatar);
//                Log.d("//==Update", strName + " | " + strPrice);

            }

            btnSave.setOnClickListener(view -> {
                String id = strId;
                String ten = edTen.getText().toString();
                String gia = edGia.getText().toString();
                String mota = edMota.getText().toString();
                String avatar = edAvatar.getText().toString();

                //kiểm tra rỗng
                if (TextUtils.isEmpty(ten) || TextUtils.isEmpty(gia) ||
                        TextUtils.isEmpty(mota) || TextUtils.isEmpty(avatar)) {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Không được để trống");
                } else {
                    //kiểm tra
                    if (ten.equalsIgnoreCase(strName) && gia.equalsIgnoreCase(strPrice)
                            && mota.equalsIgnoreCase(strMota) && avatar.equalsIgnoreCase(strAvatar)) {
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText("Bạn chưa sửa thông tin");
                        Toast.makeText(context, "Bạn chưa cập nhật", Toast.LENGTH_SHORT).show();

                    } else {
                        dao.update(context,mDatabase, id, ten, gia, mota, avatar);
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
            btnCancel.setOnClickListener(view -> {
                dialog.dismiss();
                //

                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                //
            });
        }
        dialog.show();

    }
    private  void  openDialogDelete(final  Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete);
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        // Khi chạm bên ngoài dialog sẽ ko đóng
        dialog.setCanceledOnTouchOutside(false);

            TextView tvXoa;
            Button btnCancel1, btnDelete1;
            tvXoa = dialog.findViewById(R.id.tvXoa);
            btnDelete1 = dialog.findViewById(R.id.btnDelete1);
            btnCancel1 = dialog.findViewById(R.id.btnCancel1);

            tvXoa.setText("Bạn muốn xóa \n\n" + "Tên: "+ strName);

           btnCancel1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   dialog.cancel();
               }
           });

            btnDelete1.setOnClickListener(view -> {
                dao.delete(context,mDatabase,strId);
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

        dialog.show();

    }
    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    //tạo 1 class item_product
    public class ProductVH extends RecyclerView.ViewHolder{

        TextView ten, gia;
        ImageView imgArt;
        CardView detail;

        public ProductVH(View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.detail);
            ten = itemView.findViewById(R.id.ten);
            gia = itemView.findViewById(R.id.gia);
            imgArt = itemView.findViewById(R.id.imgArt);
        }
    }
}
