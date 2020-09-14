package com.example.contactapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FragmentDetailContact extends Fragment {

    Contact contact;
    ImageView imgPhone,imgMessage;
    TextView txtIcon,txtName,txtPhone,txtEmail;
    private final int REQUEST_CALL_PHONE = 109;
    private final int REQUEST_MESSAGE = 110;


    public FragmentDetailContact(Contact contact) {
        this.contact = contact;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_detail_contact, null, false);
        initControl(view);
        bindEvent();
        return view;
    }

    private void initControl(final View view){
        setHasOptionsMenu(false);
        imgMessage = view.findViewById(R.id.imgMessage);
        imgPhone = view.findViewById(R.id.imgPhone);
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

    private void bindEvent(){
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
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.SEND_SMS);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},REQUEST_MESSAGE);
                } else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phonenummber, null)));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL_PHONE){
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.phonenummber));
                startActivity(intent);
            } else {
                Log.d("TAG", "Call Permission Not Granted");
            }
        }
        else if(requestCode == REQUEST_MESSAGE){
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.phonenummber, null)));
            } else {
                Log.d("TAG", "Send sms Permission Not Granted");
            }
        }
    }
}
