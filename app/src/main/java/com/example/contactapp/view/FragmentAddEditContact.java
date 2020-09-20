package com.example.contactapp.view;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.contactapp.config.Utils;
import com.example.contactapp.model.Contact;
import com.example.contactapp.db.ContactDatabase;
import com.example.contactapp.model.OnChangeContact;
import com.example.contactapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragmentAddEditContact extends Fragment {
    ImageView imgAvatar;
    EditText edtName, edtPhone, edtEmail;
    private byte[] image;
    public static final int PICK_IMAGE = 1997;
    private ContactDatabase contactDatabase;
    OnChangeContact changeContact;
    TextView txtIcon;
    Button btnUpdate;
    LinearLayout layout;
    Snackbar snackbar;

    public FragmentAddEditContact(ContactDatabase contactDatabase) {
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

    private void initControl(final View view) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        layout = view.findViewById(R.id.layout_add_edit_contact);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        txtIcon = view.findViewById(R.id.txtIcon);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        edtName = view.findViewById(R.id.edtName);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtEmail = view.findViewById(R.id.edtEmail);
        actionBar.setTitle(R.string.add_contact);

    }

    private void bindEvent() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        txtIcon.setOnClickListener(new View.OnClickListener() {
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
        if (menuID == R.id.btnAddDone) {
            if (!Utils.isValidName(edtName.getText().toString()))
                Utils.showSnackbar("Tên không hợp lệ", snackbar, layout);
            else if (!Utils.isValidPhoneNumber(edtPhone.getText().toString()))
                Utils.showSnackbar("Số điện thoại không hợp lệ", snackbar, layout);
            else if (!Utils.isValidEmail(edtEmail.getText().toString()))
                Utils.showSnackbar("Email không hợp lệ", snackbar, layout);
            else {
                final Contact contact = new Contact(
                        edtName.getText().toString(),
                        edtPhone.getText().toString(),
                        edtEmail.getText().toString(),
                        image
                );
                if (contactDatabase.insertContact(contact) > 0) {
                    Utils.showSnackbar(getString(R.string.add_success), snackbar, layout);
                    changeContact.onChange();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Log.d("RESULT", "OK");
            assert data != null;
            Uri imageUri = data.getData();
            try {
                final Bitmap bitmapSelection = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                final Bitmap bitmapScale = Bitmap.createScaledBitmap(bitmapSelection, 150, bitmapSelection.getHeight() * 150 / bitmapSelection.getWidth(), false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapScale.compress(Bitmap.CompressFormat.PNG, 100, stream);
                image = stream.toByteArray();
                imgAvatar.setImageBitmap(bitmapScale);
                imgAvatar.setVisibility(View.VISIBLE);
                txtIcon.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        changeContact = (OnChangeContact) context;
    }

}
