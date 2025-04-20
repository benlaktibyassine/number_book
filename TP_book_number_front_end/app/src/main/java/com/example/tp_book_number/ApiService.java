package com.example.tp_book_number;



import com.example.tp_book_number.beans.Contact;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/contact/batch")
    Call<Void> saveContactsBatch(@Body List<Contact> contacts);
    @GET("api/contact/")
    Call<List<Contact>> getAllContacts();

    @GET("api/contact/name/{name}")
    Call<Contact> getContactByName(@Path("name") String name);

    @POST("api/contact/")
    Call<Contact> createContact(@Body Contact contact);

    @DELETE("api/contact/id/{id}")
    Call<Void> deleteContact(@Path("id") Long id);
}
