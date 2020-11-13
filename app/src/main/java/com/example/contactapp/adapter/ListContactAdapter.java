package com.example.contactapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.contactapp.R;
import com.example.contactapp.model.Contact;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListContactAdapter extends BaseAdapter {
    private List<Contact> contactList;
    private LayoutInflater layoutInflater;
    private Context context;
    String[] colors = new String[]{"#5E97F6","#9CCC65","#FF8A65","#9FA8DA","#90A4AE","#AED581","#F6BF26","#A1887F","#FFA726"};
    public ListContactAdapter(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return contactList.get(i).id;
    }

    @SuppressLint("CheckResult")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_items_contact, null);
            holder = new ViewHolder();
            holder.txtIcon = (TextView) convertView.findViewById(R.id.txtIcon);
            holder.txtContactName = (TextView) convertView.findViewById(R.id.txtContactName);
            holder.imageView = convertView.findViewById(R.id.imgAvatar);
            draw.setColor(context.getResources().getColor(R.color.colorAccent));
            holder.txtIcon.setBackground(draw);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = this.contactList.get(i);
        holder.txtContactName.setText(contact.name);
        if (contact.image != null){
            Glide.with(context).load(contact.image).centerCrop().into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.txtIcon.setVisibility(View.INVISIBLE);

        }else{
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.txtIcon.setText(contact.name.substring(0,1).toUpperCase());
        }
        return convertView;
    }


    static class ViewHolder{
        TextView txtIcon, txtContactName;
        CircleImageView imageView;
    }
}
