package com.example.contactapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnChangeContact {
    ContactDatabase db;
    FloatingActionButton btnAdd;
    List<Contact> listContact = new ArrayList<>();
    ListView lvContact;
    private final int READ_STORAGE = 146;
    private final int WRITE_STORAGE = 178;
    //    Toolbar toolbar;
    private int type = 1;
    private ListContactAdapter listContactAdapter;
    private final String SECRET_KEY = "pxtabxb13rwl4al";
    private final String APP_KEY = "qji32i4jlv5xx0u";
    private final String TOKEN = "XPKp-FHsag8AAAAAAAAAAfLVhedRgoefGekbIYhREeHMkDejRl5KT9NfD8wvmOym";
    private final String TAG = "TAG";
    private static DbxClientV2 sDbxClient = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        initControl();
        bindEvent(fragmentManager);
        int permissionCheckREAD = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckREAD != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE);
        }

        if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE);
        }

        listContact = db.getContact();
        listContactAdapter = new ListContactAdapter(listContact, this);
        lvContact.setAdapter(listContactAdapter);
        sDbxClient = new DbxClientV2(DbxRequestConfigFactory.getRequestConfig(), TOKEN);
    }

    private void initControl() {
        lvContact = findViewById(R.id.lvContact);
        btnAdd = findViewById(R.id.btnAddContact);
        db = new ContactDatabase(this);
        preferences = getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    private void bindEvent(final FragmentManager fragmentManager) {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction t = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                t.add(R.id.fragmentContact, new FragmentAddEditContact(db), "TAG");
                t.addToBackStack(null);
                t.commit();
                type = 2;
                invalidateOptionsMenu();
                btnAdd.hide();
            }
        });


        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = listContact.get(i);
                FragmentTransaction t = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                t.add(R.id.fragmentContact, new FragmentDetailContact(contact, db), "TAG");
                t.addToBackStack(null);
                t.commit();
                type = 3;
                invalidateOptionsMenu();
                btnAdd.hide();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.btnSync) {
            Toast.makeText(this, "Đang tạo file vào bộ nhớ", Toast.LENGTH_SHORT).show();
            String rs = new Gson().toJson(listContact);
            File dirRoot = Environment.getExternalStorageDirectory();
            File fileJson = new File(dirRoot + "/contact.txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fileJson);
                fos.write(rs.getBytes());
            } catch (Exception ignored) {
                Log.e("aa", ignored.toString());
            } finally {
                try {
                    assert fos != null;
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(this, "Đã tạo file thành công, bắt đầu quá trình upload!", Toast.LENGTH_SHORT).show();
            new UploadFileTask(this, sDbxClient, new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {
                    Toast.makeText(MainActivity.this, "Upload đám mấy thành công", Toast.LENGTH_SHORT).show();
                    editor.putString("id", result.getId());
                    editor.putString("name", result.getName());
                    editor.putString("rev", result.getRev());
                    editor.putLong("size", result.getSize());
                    editor.putString("pathLower", result.getPathLower());
                    editor.commit();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(MainActivity.this, "Upload không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).execute(fileJson);
        } else if (menuId == R.id.btnSyncUp){
            Toast.makeText(this, "Đang thực hiện quá trình tải xuống!", Toast.LENGTH_SHORT).show();
            String idFile = preferences.getString("id", null);
            new DownloadFileTask(this, sDbxClient, new DownloadFileTask.Callback() {
                @Override
                public void onDownloadComplete(File result) {
                    try {
                        Toast.makeText(MainActivity.this, "Tải xuống thành công! Bắt đầu quá trình đồng bộ", Toast.LENGTH_SHORT).show();
                        FileInputStream inputStream = new FileInputStream(result);
                        StringBuilder stringBuilder = new StringBuilder();
                        byte[] buffer = new byte[1024];
                        int n;
                        while ((n = inputStream.read(buffer)) != -1) {
                            stringBuilder.append(new String(buffer, 0, n));
                        }
                        JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                        db.deleteAll();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objTemp = jsonArray.getJSONObject(i);
                            ByteArrayOutputStream output = null;
                            if (objTemp.has("image")) {
                                JSONArray arrTemp = objTemp.getJSONArray("image");
                                output = new ByteArrayOutputStream();
                                for (int j = 0; j < arrTemp.length(); j++) {
                                    output.write(arrTemp.getInt(j));
                                }
                            }
                            db.insertContact(
                                    new Contact(objTemp.getString("name"),
                                            objTemp.getString("phonenummber"),
                                            objTemp.getString("email"),
                                            output == null ? null : output.toByteArray()));
                        }
                        listContact = db.getContact();
                        lvContact.setAdapter(new ListContactAdapter(listContact, MainActivity.this));
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Lỗi đồng bộ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("asdasd", e.getMessage());
                }
            }).execute(new FileMetadata(
                    "contact.txt",
                    idFile,
                    new Date(),
                    new Date(),
                    preferences.getString("rev", null),
                    preferences.getLong("size", 0))
            );

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (type == 1) {
            menuInflater.inflate(R.menu.menu, menu);
        } else if (type == 2) {
            menuInflater.inflate(R.menu.menu_done, menu);
        } else {
            menuInflater.inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (type != 1) {
            type = 1;
            invalidateOptionsMenu();
            btnAdd.show();
            getSupportActionBar().setTitle("Danh bạ");
        }
        super.onBackPressed();
    }

    @Override
    public void onChange() {
        listContact = db.getContact();
        lvContact.setAdapter(new ListContactAdapter(listContact, this));
//        listContactAdapter.notifyDataSetChanged();
        onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.phonenummber));
//                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền đọc bộ nhớ để đồng bộ!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == WRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phonenummber, null)));
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền ghi bộ nhớ để đồng bộ!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}