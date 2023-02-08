package com.example.techy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myref = database.getReference("Users");
    
    public static Boolean isAdmin;

    RecyclerView recyclerView;
    DatabaseReference myrefMenu = database.getReference("Menu");
    Adapter adapter;
    static ArrayList<Item> list;

    Button favBtn, ordinazioneBtn;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(Menu.NONE, 1, Menu.NONE, "Esci");

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

       logout();

       return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        favBtn = findViewById(R.id.favBtn);
        ordinazioneBtn = findViewById(R.id.ordinazioniBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //controllo se l'utente è un admin --------------------

        DatabaseReference finalRef = myref.child(user.getUid()).child("isAdmin");
        finalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 isAdmin = dataSnapshot.getValue(Boolean.class);
                 System.out.println(isAdmin);
                 if(isAdmin){

                     FloatingActionButton fab1 = findViewById(R.id.add_fab);
                     fab1.setVisibility(View.VISIBLE);


                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //------------------------------------------------------
        //Toast.makeText(getApplicationContext(),user.getUid(),Toast.LENGTH_LONG).show();


        Button menuEsciBtn = findViewById(R.id.menuEsci);
        menuEsciBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerForContextMenu(view);
                openContextMenu(view);
                unregisterForContextMenu(view);
            }
        });

        ordinazioneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Ordinazioni.class);
                startActivity(intent);
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, favActivity.class);
                startActivity(intent);
            }
        });

        Button menuCompletoBtn = findViewById(R.id.allMenuButton);
        menuCompletoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList();

            }
        });

        Button hamburgerBtn = findViewById(R.id.hamburgerButton);
        hamburgerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logout();
                showList("Hamburger");

            }
        });

        Button pizzaBtn = findViewById(R.id.pizzeButton);
        pizzaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList("Pizza");

            }
        });

        Button dessertBtn = findViewById(R.id.DessertButton);
        dessertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList("Dessert");

            }
        });

        Button drinkBtn = findViewById(R.id.DrinksButton);
        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList("Drink");

            }
        });


        FloatingActionButton addItemPage = findViewById(R.id.add_fab);
        addItemPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Home.this, addNewItem.class);
                startActivity(intent);

            }
        });

        //recyclerview
        showList();


    }


    private void showList(String categoria){
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = myrefMenu.orderByChild("categoriaItem").equalTo(categoria);


        list = new ArrayList<>();
        adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                        Item item = new Item(ds.child("nomeItem").getValue().toString(), ds.child("categoriaItem").getValue().toString(), ds.child("immagine").getValue().toString(), ds.child("descrizioneItem").getValue().toString(), ds.child("prezzoItem").getValue().toString(), ds.child("itemKey").getValue().toString());
                        list.add(item);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showList(){
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);

        myrefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                    Item item = new Item(ds.child("nomeItem").getValue().toString(), ds.child("categoriaItem").getValue().toString(), ds.child("immagine").getValue().toString(), ds.child("descrizioneItem").getValue().toString(), ds.child("prezzoItem").getValue().toString(), ds.child("itemKey").getValue().toString());
                    list.add(item);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Home.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



}