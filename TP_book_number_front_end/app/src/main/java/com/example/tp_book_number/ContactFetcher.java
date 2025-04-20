package com.example.tp_book_number;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.example.tp_book_number.beans.Contact;
import java.util.ArrayList;
import java.util.List;

public class ContactFetcher {

    public static List<Contact> fetchPhoneContacts(ContentResolver contentResolver) {
        List<Contact> contacts = new ArrayList<>();

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Clean and format number
                number = number.replaceAll("[^0-9+]", "");

                contacts.add(new Contact(name, number));
            }
            cursor.close();
        }

        return contacts;
    }
}
