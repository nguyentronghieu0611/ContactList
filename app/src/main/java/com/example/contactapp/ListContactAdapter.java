package com.example.contactapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

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

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        Random r = new Random();
        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        int i1 = r.nextInt(9);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_items_contact, null);
            holder = new ViewHolder();
            holder.color = Color.parseColor(colors[i1]);
            holder.txtIcon = (TextView) convertView.findViewById(R.id.txtIcon);
            holder.txtContactName = (TextView) convertView.findViewById(R.id.txtContactName);
            draw.setColor(holder.color);
            holder.txtIcon.setBackground(draw);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = this.contactList.get(i);
        holder.txtIcon.setText(contact.name.substring(0,1).toUpperCase());
        holder.txtContactName.setText(contact.name);
        return convertView;
    }


    static class ViewHolder{
        int color;
        TextView txtIcon, txtContactName;
    }
}
