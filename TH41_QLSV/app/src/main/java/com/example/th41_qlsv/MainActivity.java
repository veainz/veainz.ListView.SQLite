package com.example.th41_qlsv;

import android.content.ContentValues;
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
        //tao list view
        lv = findViewById(R.id.lv);
        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,mylist);
        lv.setAdapter(myadapter);
        //Tao va mo CSDL Database;
        mydatabase = openOrCreateDatabase("qlsinhvien.db",MODE_PRIVATE,null);
        //Tao DTB chua dulien
        try {
            String sql = "CREATE TABLE tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            mydatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("ERROR", "Bang da ton tai");
        }
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String malop = edtMaLop.getText().toString();
                int n = mydatabase.delete("tbllop", "malop = ?", new String[] {malop});
                String msg = "";
                if (n==0) {
                    msg = "Khong ghi nhan trong CSDL";
                } else {
                    msg = "Ban ghi thu " + n + " da duoc xoa";
                }
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int siso = Integer.parseInt(edtSiSo.getText().toString());
                String malop = edtMaLop.getText().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("siso", siso);
                int n = mydatabase.update("tbllop", myvalue,"malop = ?", new String[]{malop});
                String msg = "";
                if (n==0){
                    msg = "Khong co ban ghi trong CSDL";
                } else {
                    msg = "Ban ghi so "+n+" da duoc cap nhat.";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        bntInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtMaLop.getText().toString();
                String tenlop = edtTenLop.getText().toString();
                int siso = Integer.parseInt(edtSiSo.getText().toString());
                ContentValues myvalue = new ContentValues(); myvalue.put("malop", malop);
                myvalue.put("tenlop", tenlop);
                myvalue.put("siso", siso);
                String msg = "";
                if (mydatabase.insert("tbllop", null, myvalue) == -1)
                {
                    msg = "Khong the ghi nhan, hay thu lai!";
                }
                else {
                    msg ="Da ghi nhan thong tin";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}