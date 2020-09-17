package com.example.contactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnChangeContact {
    ContactDatabase db;
    FloatingActionButton btnAdd;
    List<Contact> listContact = new ArrayList<>();
    ListView lvContact;
//    Toolbar toolbar;
    private int type = 1;
    private ListContactAdapter listContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        initControl();
        bindEvent(fragmentManager);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            toolbar = (Toolbar) findViewById(R.id.toolbar);
//        }
//        setSupportActionBar(toolbar);
//        db.insertContact(new Contact("Nguyễn Trọng Hiếu","0334420708","nguyentronghieu0611@gmail.com",null));
        listContact = db.getContact();
        listContactAdapter = new ListContactAdapter(listContact,this);
        lvContact.setAdapter(listContactAdapter);
    }

    private void initControl(){
        lvContact = findViewById(R.id.lvContact);
        btnAdd = findViewById(R.id.btnAddContact);
        db = new ContactDatabase(this);
    }

    private void bindEvent(final FragmentManager fragmentManager){
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
                t.add(R.id.fragmentContact, new FragmentDetailContact(contact,db), "TAG");
                t.addToBackStack(null);
                t.commit();
                type=3;
                invalidateOptionsMenu();
                btnAdd.hide();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();
        if(menuId==R.id.btnSync){
            String rs = new Gson().toJson(listContact);
            File dirRoot = Environment.getExternalStorageDirectory();
            File fileJson = new File(dirRoot + "/contact.txt");
            FileOutputStream fos = null;
            try{
                 fos = new FileOutputStream(fileJson);
                 fos.write(rs.getBytes());
            }catch (Exception ignored){
                Log.e("aa",ignored.toString());
            }finally {
                try {
                    assert fos != null;
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(type==1){
            menuInflater.inflate(R.menu.menu,menu);
        }else if(type==2){
            menuInflater.inflate(R.menu.menu_done,menu);
        }
        else{
            menuInflater.inflate(R.menu.menu_edit,menu);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(type!=1){
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
        lvContact.setAdapter(new ListContactAdapter(listContact,this));
//        listContactAdapter.notifyDataSetChanged();
        onBackPressed();
    }
}