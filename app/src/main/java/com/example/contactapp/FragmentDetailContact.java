package com.example.contactapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDetailContact extends Fragment {

    Contact contact;
    TextView txtIcon,txtName,txtPhone,txtEmail;


    public FragmentDetailContact(Contact contact) {
        this.contact = contact;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_detail_contact, null, false);
        initControl(view);
        return view;
    }

    private void initControl(final View view){
        txtName = view.findViewById(R.id.txtName);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtIcon = view.findViewById(R.id.txtIcon);
        if(contact.image!=null && contact.image.length>0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(contact.image,0,contact.image.length);
            txtIcon.setBackground(new BitmapDrawable(getResources(),bitmap));
            txtIcon.setText("");
        }
        txtName.setText(contact.name);
        txtPhone.setText(contact.phonenummber);
        txtEmail.setText(contact.email);
    }
}
