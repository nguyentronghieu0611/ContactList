package com.example.contactapp.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
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
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.contactapp.R;
import com.example.contactapp.adapter.ListContactAdapter;
import com.example.contactapp.config.DbxRequestConfigFactory;
import com.example.contactapp.config.Utils;
import com.example.contactapp.db.ContactDatabase;
import com.example.contactapp.model.Contact;
import com.example.contactapp.model.OnChangeContact;
import com.example.contactapp.task.DownloadFileTask;
import com.example.contactapp.task.GetFileIdTask;
import com.example.contactapp.task.UploadFileTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    private int type = 1;
    private ListContactAdapter listContactAdapter;
    private final String SECRET_KEY = "pxtabxb13rwl4al";
    private final String APP_KEY = "qji32i4jlv5xx0u";
    private final String TOKEN = "XPKp-FHsag8AAAAAAAAAAfLVhedRgoefGekbIYhREeHMkDejRl5KT9NfD8wvmOym";
    private final String TAG = "TAG";
    private static DbxClientV2 sDbxClient = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Snackbar snackbar;
    private ConstraintLayout layout;
    SearchView searchView;

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
        layout = findViewById(R.id.layout_main);
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
        final ProgressDialog dialog;
        int menuId = item.getItemId();
        if (menuId == R.id.btnSync) {
            dialog = ProgressDialog.show(MainActivity.this, "",
                    getString(R.string.creating_file), true);
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
            dialog.setMessage(getString(R.string.start_upload));
            new UploadFileTask(this, sDbxClient, new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {
                    editor.putString("id", result.getId());
                    editor.putString("name", result.getName());
                    editor.putString("rev", result.getRev());
                    editor.putLong("size", result.getSize());
                    editor.putString("pathLower", result.getPathLower());
                    editor.commit();
                    dialog.dismiss();
                    Utils.showSnackbar(getString(R.string.upload_success),snackbar,layout);
                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                    Utils.showSnackbar(getString(R.string.upload_fail)+e.getMessage(),snackbar,layout);
                }
            }).execute(fileJson);
        } else if (menuId == R.id.btnSyncUp){
            dialog = ProgressDialog.show(MainActivity.this, "",
                    getString(R.string.downloading), true);

            new GetFileIdTask(this, sDbxClient, new GetFileIdTask.Callback() {
                @Override
                public void onGetIdComplete(JSONObject result) {
                    if(result!=null){
                        try {
                            new DownloadFileTask(MainActivity.this, sDbxClient, new DownloadFileTask.Callback() {
                                @Override
                                public void onDownloadComplete(File result) {
                                    try {
                                        dialog.setMessage(getString(R.string.start_sync_after_download));
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
                                        dialog.dismiss();
                                        Utils.showSnackbar(getString(R.string.sync_success),snackbar,layout);
                                    } catch (Exception e) {
                                        Utils.showSnackbar(getResources().getString(R.string.sync_fail)+e.getMessage(),snackbar,layout);
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    Utils.showSnackbar("Lỗi: "+e.getMessage(),snackbar,layout);
                                    dialog.dismiss();
                                }
                            }).execute(new FileMetadata(
                                    "contact.txt",
                                    result.getString("id"),
                                    new Date(),
                                    new Date(),
                                    result.getString("rev"),
                                    result.getInt("size"))
                            );
                        } catch (JSONException e) {
                            dialog.dismiss();
                            Utils.showSnackbar(getString(R.string.file_not_found),snackbar,layout);
                        }
                    }
                    else {
                        dialog.dismiss();
                        Utils.showSnackbar(getString(R.string.file_not_found),snackbar,layout);
                    }

                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                    Utils.showSnackbar(getResources().getString(R.string.sync_fail)+e.getMessage(),snackbar,layout);
                }
            }).execute(new Object());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (type == 1) {
            menuInflater.inflate(R.menu.menu, menu);
            initSearch(menu);
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
            getSupportActionBar().setTitle(R.string.contact);
        }
        super.onBackPressed();
    }

    @Override
    public void onChange() {
        listContact = db.getContact();
        lvContact.setAdapter(new ListContactAdapter(listContact, this));
        onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.phonenummber));
//                startActivity(intent);
            } else {
                Utils.showSnackbar(getString(R.string.please_grant_read),snackbar,layout);
            }
        } else if (requestCode == WRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phonenummber, null)));
            } else {
                Utils.showSnackbar(getString(R.string.please_grant_write),snackbar,layout);
            }
        }
    }

    private void initSearch(Menu menu){
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView =
                (SearchView) searchItem.getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #AEAEAE>" + getResources().getString(R.string.hintSearchMess) + "</font>"));
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","CLICKED SEARCH");
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d("TAG","CLICKED CLOSE SEARCH");
                listContact = db.getContact();
                lvContact.setAdapter(new ListContactAdapter(listContact, MainActivity.this));
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("query",query);
                listContact = db.searchContact(query.toUpperCase());
                lvContact.setAdapter(new ListContactAdapter(listContact, MainActivity.this));
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
//                if(!newText.isEmpty())
//                    initStateSearch(true);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("newText",newText);
//                        listError = db.searchError(newText.toUpperCase());
//                        lvError.setAdapter(new ErrorAdapter(listError,ErrorActivity.this,fragmentManager,db));
//                        db.insertHistory(new SearchHistory(user_id,newText));
//                    }
//                },1500);

                return true;
            }
        });
    }

}