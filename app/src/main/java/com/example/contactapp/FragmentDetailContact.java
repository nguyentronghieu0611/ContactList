package com.example.contactapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentDetailContact extends Fragment {
    byte[] image = null;
    Contact contact;
    ImageView imgPhone, imgMessage;
    TextView txtIcon;
    private final int REQUEST_CALL_PHONE = 109;
    private final int REQUEST_MESSAGE = 110;
    ContactDatabase contactDatabase;
    OnChangeContact changeContact;
    EditText edtName,edtPhone,edtEmail;
    Button btnUpdate;
    boolean isUpdate = false;
    CircleImageView imgAvatar;

    public FragmentDetailContact(Contact contact, ContactDatabase contactDatabase) {
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
        final View view = inflater.inflate(R.layout.fragment_detail_contact, null, false);
        initControl(view);
        bindEvent();
        return view;
    }

    private void initControl(final View view) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Chi tiết");
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        imgMessage = view.findViewById(R.id.imgMessage);
        imgPhone = view.findViewById(R.id.imgPhone);
        edtName = view.findViewById(R.id.txtName);
        edtPhone = view.findViewById(R.id.txtPhone);
        edtEmail = view.findViewById(R.id.txtEmail);
        txtIcon = view.findViewById(R.id.txtIcon);
        if (contact.image != null && contact.image.length > 0) {
            image = contact.image;
            Bitmap bitmap = BitmapFactory.decodeByteArray(contact.image, 0, contact.image.length);
            imgAvatar.setImageBitmap(bitmap);
            imgAvatar.setVisibility(View.VISIBLE);
            txtIcon.setVisibility(View.GONE);
        }
        else {
            txtIcon.setText(contact.name.substring(0,1).toUpperCase());
            imgAvatar.setVisibility(View.GONE);
            txtIcon.setVisibility(View.VISIBLE);
        }
        edtName.setText(contact.name);
        edtPhone.setText(contact.phonenummber);
        edtEmail.setText(contact.email);
    }

    private void bindEvent() {
        imgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_CALL_PHONE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.phonenummber));
                    startActivity(intent);
                }


            }
        });

        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_MESSAGE);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phonenummber, null)));
                }
            }
        });

        txtIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), FragmentAddEditContact.PICK_IMAGE);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), FragmentAddEditContact.PICK_IMAGE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUpdate) {
                    edtName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtName, InputMethodManager.SHOW_IMPLICIT);
                    edtName.setEnabled(true);
                    edtPhone.setEnabled(true);
                    edtEmail.setEnabled(true);
                    btnUpdate.setText("Hoàn Thành");
                    isUpdate = true;
                }
                else{
                    edtName.setEnabled(false);
                    edtPhone.setEnabled(false);
                    edtEmail.setEnabled(false);
                    btnUpdate.setText("Cập Nhật");
                    contact.name = edtName.getText().toString();
                    contact.phonenummber = edtPhone.getText().toString();
                    contact.email = edtEmail.getText().toString();
                    contact.image = image;
                    int rs = contactDatabase.updateContact(contact);
                    if(rs>0){
                        changeContact.onChange();
                        Toast.makeText(getContext(),"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                    }
                    isUpdate = false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.phonenummber));
                startActivity(intent);
            } else {
                Log.d("TAG", "Call Permission Not Granted");
            }
        } else if (requestCode == REQUEST_MESSAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phonenummber, null)));
            } else {
                Log.d("TAG", "Send sms Permission Not Granted");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Bạn có chắc chắn xóa người dùng?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = contactDatabase.deleteContact(contact);
                        if (i > 0) {
                            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                            changeContact.onChange();
                        }
                    }
                })
                .setNegativeButton("Không", null)
                .show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        changeContact = (OnChangeContact) context;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FragmentAddEditContact.PICK_IMAGE && resultCode == RESULT_OK) {
            Log.d("RESULT", "OK");
            assert data != null;
            Uri imageUri = data.getData();
            try {
                final Bitmap bitmapSelection = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapSelection.compress(Bitmap.CompressFormat.PNG, 30, stream);
                image = stream.toByteArray();
                imgAvatar.setImageBitmap(bitmapSelection);
                txtIcon.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
