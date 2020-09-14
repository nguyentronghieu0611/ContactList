package com.example.contactapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragmentAddEditContact extends Fragment {
    Contact contact;
    ImageView imgAvatar;
    EditText edtName, edtPhone, edtEmail;
    private byte[] image;
    private final int PICK_IMAGE = 1997;
    private ContactDatabase contactDatabase;

    public FragmentAddEditContact(Contact contact,ContactDatabase contactDatabase) {
        this.contact = contact;
        this.contactDatabase = contactDatabase;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        assert actionBar != null;
        actionBar.setIcon(R.drawable.ic_complete);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        edtName = view.findViewById(R.id.edtName);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtEmail = view.findViewById(R.id.edtEmail);
        if(contact==null)
            actionBar.setTitle("Thêm liên lạc");
        else{
            actionBar.setTitle("Cập nhật liên lạc");
            edtName.setText(contact.name);
            edtPhone.setText(contact.phonenummber);
            edtEmail.setText(contact.email);
        }
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuID = item.getItemId();
        if(menuID == R.id.btnAddDone){
            final Contact contact = new Contact(
                  edtName.getText().toString(),
                  edtPhone.getText().toString(),
                  edtEmail.getText().toString(),
                  image
            );
            if(contactDatabase.insertContact(contact) > 0){
                Toast.makeText(getActivity(),"Thêm dữ liệu thành công",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Log.d("RESULT","OK");
            assert data != null;
            Uri imageUri = data.getData();
            try {
                final Bitmap bitmapSelection = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(),imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapSelection.compress(Bitmap.CompressFormat.PNG, 30, stream);
                image = stream.toByteArray();
                imgAvatar.setImageBitmap(bitmapSelection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
