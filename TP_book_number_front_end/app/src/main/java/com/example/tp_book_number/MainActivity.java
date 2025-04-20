package com.example.tp_book_number;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp_book_number.adapter.ContactAdapter;
import com.example.tp_book_number.beans.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 2;
    private static final String PREFS_NAME = "ContactsAppPrefs";
    private static final String KEY_FIRST_RUN = "isFirstRun";

    private ApiService apiService;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ContactAdapter adapter;
    private List<Contact> contactsList = new ArrayList<>();
    private List<Contact> filteredContactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiService = RetrofitClient.getApiService();
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this, filteredContactsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // Configuration du FAB
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(view -> {
            Toast.makeText(this, "Ajouter un nouveau contact", Toast.LENGTH_SHORT).show();
            // Ici vous pourriez ouvrir une activité pour ajouter un contact
        });

        // Configuration de la SearchView
        setupSearchView();

        // Vérification des permissions
        checkPermissionsAndLoadContacts();
    }

    private void checkPermissionsAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CALL_PHONE
                    },
                    PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            // Vérifier si c'est la première exécution
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isFirstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true);

            if (isFirstRun) {
                // Si c'est la première exécution, synchroniser les contacts
                syncContactsToServer(true);

                // Marquer que ce n'est plus la première exécution
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_FIRST_RUN, false);
                editor.apply();
            } else {
                // Si ce n'est pas la première exécution, juste charger les contacts
                fetchContactsFromServer();
            }
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContacts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
    }

    private void filterContacts(String query) {
        if (query.isEmpty()) {
            filteredContactsList.clear();
            filteredContactsList.addAll(contactsList);
        } else {
            String queryLowerCase = query.toLowerCase();
            filteredContactsList.clear();
            filteredContactsList.addAll(contactsList.stream()
                    .filter(contact ->
                            contact.getName().toLowerCase().contains(queryLowerCase) ||
                                    contact.getNumber().contains(query))
                    .collect(Collectors.toList()));
        }
        adapter.updateContacts(filteredContactsList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            // Vérifier si toutes les permissions sont accordées
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                // Vérifier si c'est la première exécution
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isFirstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true);

                if (isFirstRun) {
                    // Si c'est la première exécution, synchroniser les contacts
                    syncContactsToServer(true);

                    // Marquer que ce n'est plus la première exécution
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_FIRST_RUN, false);
                    editor.apply();
                } else {
                    // Si ce n'est pas la première exécution, juste charger les contacts
                    fetchContactsFromServer();
                }
            } else {
                Toast.makeText(this, "Les permissions sont nécessaires pour utiliser l'application", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void syncContactsToServer(boolean isFirstRun) {
        List<Contact> contacts = ContactFetcher.fetchPhoneContacts(getContentResolver());

        apiService.saveContactsBatch(contacts).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String message = isFirstRun ?
                            "Première synchronisation réussie des  contacts" :
                            "Synchronisation  réussie des  contacts";

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    fetchContactsFromServer();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Erreur serveur: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                    fetchContactsFromServer();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Erreur réseau: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                fetchContactsFromServer();
            }
        });
    }

    private void fetchContactsFromServer() {
        apiService.getAllContacts().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactsList = response.body();
                    filteredContactsList.clear();
                    filteredContactsList.addAll(contactsList);
                    adapter.updateContacts(filteredContactsList);
                    Toast.makeText(MainActivity.this,
                            contactsList.size() + " contacts chargés",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Échec du chargement des contacts: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Erreur réseau lors du chargement des contacts: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            // Synchronisation manuelle explicite demandée par l'utilisateur
            syncContactsToServer(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}