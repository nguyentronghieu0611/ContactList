package com.example.contactapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class FragmentAddEditContact extends Fragment {
    Contact contact;
    ImageView imgAvatar;
    private final int PICK_IMAGE = 1997;

    public FragmentAddEditContact(Contact contact) {
        this.contact = contact;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_edit_contact, null, false);
        initControl(view);
        bindEvent();
        return view;
    }

    private void initControl(final View view){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(contact==null)
            actionBar.setSubtitle("Thêm liên lạc");
        else
            actionBar.setSubtitle("Cập nhật liên lạc");
        imgAvatar = view.findViewById(R.id.imgAvatar);
    }

    private void bindEvent(){
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK) {
            Log.d("RESULT","OK");
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                imgAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
