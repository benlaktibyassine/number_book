package com.example.tp_book_number.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp_book_number.R;
import com.example.tp_book_number.beans.Contact;

import java.util.List;
import java.util.Random;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;
    private int[] colors = {
            0xFF3F51B5, 0xFFE91E63, 0xFF9C27B0, 0xFF673AB7,
            0xFF2196F3, 0xFF009688, 0xFF4CAF50, 0xFFFF9800
    };
    private Random random = new Random();

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getNumber());

        // Définir l'initiale et une couleur aléatoire pour l'avatar
        String initial = contact.getName().substring(0, 1).toUpperCase();
        holder.tvAvatar.setText(initial);
        holder.tvAvatar.setBackgroundColor(colors[Math.abs(contact.getName().hashCode()) % colors.length]);

        // Ajouter des écouteurs pour les boutons d'appel et de SMS
        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + contact.getNumber()));
            context.startActivity(intent);
        });

        holder.btnSms.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + contact.getNumber()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateContacts(List<Contact> newContacts) {
        this.contactList = newContacts;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar;
        TextView tvName;
        TextView tvPhone;
        ImageView btnCall;
        ImageView btnSms;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnSms = itemView.findViewById(R.id.btn_sms);
        }
    }
}
