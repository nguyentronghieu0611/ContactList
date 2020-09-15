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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentDetailContact extends Fragment {

    Contact contact;
    ImageView imgPhone,imgMessage;
    TextView txtIcon,txtName,txtPhone,txtEmail;
    private final int REQUEST_CALL_PHONE = 109;
    private final int REQUEST_MESSAGE = 110;
    ContactDatabase contactDatabase;
    OnChangeContact changeContact;

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

    private void initControl(final View view){
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuID = item.getItemId();
        if(menuID == R.id.btnEdit){
            FragmentTransaction t = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.add(R.id.fragmentContact, new FragmentAddEditContact(contact,contactDatabase), "TAG");
            t.addToBackStack(null);
            t.commit();
        }
        else{
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Bạn có chắc chắn xóa người dùng?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = contactDatabase.deleteContact(contact);
                            if(i>0){
                                Toast.makeText(getContext(),"Xóa thành công",Toast.LENGTH_SHORT).show();
                                changeContact.onChange();
                                getActivity().onBackPressed();
                            }
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        changeContact = (OnChangeContact) context;
    }
}
