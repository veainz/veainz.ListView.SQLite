package com.example.th41_qlsv;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edtMaLop, edtTenLop, edtSiSo;
    Button bntInsert, btnDelete, btnUpdate;
    ListView lv;
    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtMaLop = findViewById(R.id.edtMaLop);
        edtTenLop = findViewById(R.id.edtTenLop);
        edtSiSo = findViewById(R.id.edtSiSo);
        bntInsert = findViewById(R.id.btnInsert);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        // Tạo list view
        lv = findViewById(R.id.lv);
        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        lv.setAdapter(myadapter);

        // Tạo và mở CSDL
        mydatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        // Tạo bảng chứa dữ liệu
        try {
            String sql = "CREATE TABLE tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            mydatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("ERROR", "Bảng đã tồn tại");
        }

        // Tải dữ liệu từ CSDL khi ứng dụng khởi động
        loadData();

        btnDelete.setOnClickListener(view -> {
            String malop = edtMaLop.getText().toString();
            if (malop.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập mã lớp", Toast.LENGTH_SHORT).show();
                return;
            }
            int n = mydatabase.delete("tbllop", "malop = ?", new String[]{malop});
            String msg = n == 0 ? "Không ghi nhận trong CSDL" : "Bản ghi thứ " + n + " đã được xóa";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            loadData(); // Cập nhật ListView
        });

        btnUpdate.setOnClickListener(view -> {
            if (!validateInput()) return; //!validateInput() để kiểm tra lỗi chính xác
            String malop = edtMaLop.getText().toString();
            String tenlop = edtTenLop.getText().toString();
            int siso = Integer.parseInt(edtSiSo.getText().toString());

            if (!isRecordExists(malop)) {
                Toast.makeText(MainActivity.this, "Mã lớp không tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues myvalue = new ContentValues();
            myvalue.put("tenlop", tenlop); // Thêm dòng này để cập nhật tenlop
            myvalue.put("siso", siso);

            int n = mydatabase.update("tbllop", myvalue, "malop = ?", new String[]{malop});
            String msg = n == 0 ? "Không có bản ghi trong CSDL" : "Bản ghi số " + n + " đã được cập nhật.";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            loadData(); // Cập nhật ListView
        });

        bntInsert.setOnClickListener(view -> {
            if (validateInput()) return;
            String malop = edtMaLop.getText().toString();
            String tenlop = edtTenLop.getText().toString();
            int siso = Integer.parseInt(edtSiSo.getText().toString());

            if (isRecordExists(malop)) {
                Toast.makeText(MainActivity.this, "Mã lớp đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues myvalue = new ContentValues();
            myvalue.put("malop", malop);
            myvalue.put("tenlop", tenlop);
            myvalue.put("siso", siso);
            String msg = mydatabase.insert("tbllop", null, myvalue) == -1 ? "Không thể ghi nhận, hãy thử lại!" : "Đã ghi nhận thông tin";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            loadData(); // Cập nhật ListView
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadData() {
        mylist.clear();
        Cursor cursor = mydatabase.rawQuery("SELECT * FROM tbllop", null);
        if (cursor.moveToFirst()) {
            do {
                String malop = cursor.getString(cursor.getColumnIndexOrThrow("malop"));
                String tenlop = cursor.getString(cursor.getColumnIndexOrThrow("tenlop"));
                int siso = cursor.getInt(cursor.getColumnIndexOrThrow("siso"));
                mylist.add("Mã lớp: " + malop + ", Tên lớp: " + tenlop + ", Sĩ số: " + siso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        myadapter.notifyDataSetChanged();
    }

    private boolean validateInput() {
        String malop = edtMaLop.getText().toString();
        String tenlop = edtTenLop.getText().toString();
        String sisoStr = edtSiSo.getText().toString();

        if (malop.isEmpty() || tenlop.isEmpty() || sisoStr.isEmpty()) {
            Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int siso = Integer.parseInt(sisoStr);
            if (siso <= 0) {
                Toast.makeText(MainActivity.this, "Sĩ số phải là số nguyên dương", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Sĩ số phải là số nguyên", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isRecordExists(String malop) {
        Cursor cursor = mydatabase.query("tbllop", null, "malop = ?", new String[]{malop}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
