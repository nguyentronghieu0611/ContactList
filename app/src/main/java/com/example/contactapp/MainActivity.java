package com.example.contactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ContactDatabase db;
    FloatingActionButton btnAdd;
    List<Contact> listContact = new ArrayList<>();
    ListView lvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        initControl();
        bindEvent(fragmentManager);
        db.insertContact(new Contact("Nguyễn Trọng Hiếu","0334420708","nguyentronghieu0611@gmail.com",null));
        listContact = db.getContact();
        lvContact.setAdapter(new ListContactAdapter(listContact,this));
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
                t.replace(R.id.fragmentContact, new FragmentAddEditContact(null), "TAG");
                t.addToBackStack(null);
                t.commit();
            }
        });

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = listContact.get(i);
                FragmentTransaction t = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                t.replace(R.id.fragmentContact, new FragmentDetailContact(contact), "TAG");
                t.addToBackStack(null);
                t.commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }
}