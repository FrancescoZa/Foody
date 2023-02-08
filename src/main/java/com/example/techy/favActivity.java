package com.example.techy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class favActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    RecyclerView recyclerView;
    DatabaseReference myrefFav = database.getReference("Favourite");
    DatabaseReference myrefMenu = database.getReference("Menu");
    Adapter adapter;
    Button backbtn;
    ArrayList<Item> list;

    @Override
    protected void onResume() {
        super.onResume();
        showfav();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        getSupportActionBar().hide();
        backbtn = findViewById(R.id.backBtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    int count;
    Boolean empty;

    private void showfav(){

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);
        count = 0;
        myrefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                empty = true;

                for(DataSnapshot ds : snapshot.getChildren()){
                    count++;
                    myrefFav.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshotver) {

                            if (snapshotver.hasChild(user.getUid()+"=_*_="+ds.child("itemKey").getValue().toString())) {
                                Item item = new Item(ds.child("nomeItem").getValue().toString(), ds.child("categoriaItem").getValue().toString(), ds.child("immagine").getValue().toString(), ds.child("descrizioneItem").getValue().toString(), ds.child("prezzoItem").getValue().toString(), ds.child("itemKey").getValue().toString());
                                list.add(item);
                                empty = false;

                                adapter.notifyDataSetChanged();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}