package com.example.techy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Ordinazioni extends AppCompatActivity {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    Button ordinaOraBtn, ordinazionibtn, carrelloBtn, backBtn;

    RecyclerView recyclerView;
    DatabaseReference myrefKart = database.getReference("Carrello");
    DatabaseReference myrefOrdini = database.getReference("Ordini");
    DatabaseReference myrefMenu = database.getReference("Menu");
    AdapterKart adapter;

    static ArrayList<Item> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordinazioni);
        getSupportActionBar().hide();

        backBtn = findViewById(R.id.backBtn);
        carrelloBtn = findViewById(R.id.carrelloBtn);
        carrelloBtn.setBackgroundColor(Color.parseColor("#08000000"));

        list = new ArrayList<>();

        start = true;
        showKart();

        ordinaOraBtn = findViewById(R.id.ordinaOraBtn);
        ordinazionibtn = findViewById(R.id.ordinazioniBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ordinazionibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ordini = true;
                ordinazionibtn.setBackgroundColor(Color.parseColor("#08000000"));
                carrelloBtn.setBackgroundColor(Color.TRANSPARENT);
                ordinaOraBtn.setVisibility(View.INVISIBLE);

                //cambio il margin bottom della recyclerview
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                params.setMargins(0,0,0,0);
                recyclerView.setLayoutParams(params);

                list.clear();

                showOrdini();

                adapter.notifyDataSetChanged();


            }
        });

        ordinaOraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alert = new AlertDialog.Builder(Ordinazioni.this);

                alert.setTitle("Tavolo");
                alert.setMessage("Inserisci il numero che trovi sul tavolo");

                LinearLayout layout = new LinearLayout(Ordinazioni.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(300, 20, 300, 50);

                EditText numTxt = new EditText(Ordinazioni.this);
                numTxt.setGravity(Gravity.CENTER); //centrare il testo
                numTxt.setInputType(InputType.TYPE_CLASS_NUMBER);

                layout.addView(numTxt, params);

                alert.setView(layout);

                alert.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ordinaOra(numTxt.getText().toString());

                    }
                });

                alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();



            }
        });

        carrelloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //cambio il margin bottom della recyclerview
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                params.setMargins(0,0,0,50);
                recyclerView.setLayoutParams(params);

                ordini = false;
                carrelloBtn.setBackgroundColor(Color.parseColor("#08000000"));
                ordinazionibtn.setBackgroundColor(Color.TRANSPARENT);
                ordinaOraBtn.setVisibility(View.VISIBLE);
                start = true;
                showKart();
            }
        });

    }


    private void ordinaOra(String codiceTavolo){

        //carico il contenuto del carrello (list) in Ordini
        for (Item i : list) {

            HashMap<String, String> val = new HashMap<String, String>();
            val.put("numero", i.getNumero().substring(1));
            val.put("codice", user.getUid()+"._*_."+i.getItemKeyMenu());
            val.put("codiceTavolo", codiceTavolo);

            myrefOrdini.push().setValue(val);

        }
        //elimino tutto ciò che contiene il carrello
        database.getReference("Carrello").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Ordinazioni.this, "Ordinazione avvenuta correttamente", Toast.LENGTH_SHORT).show();

            }
        });

        list.clear();

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        finish();
    }


    Boolean start;
    Boolean ordini;

    public void showKart(){

        recyclerView = findViewById(R.id.rvOrd);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list.clear();
        adapter = new AdapterKart(this, list);
        recyclerView.setAdapter(adapter);
        myrefKart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /*
                ho messo questa variabile start per un motivo:
                quando eliminavo un item, richiamava questo metodo, il risultato era una duplicazione della lista.

                es: list = item1 item2 item3
                elimino item2
                result
                    list = item1 item2 item3 item1 item3

                mettendo questo if, mi assicuro che questo metodo viene eseguito solo quando viene lanciata l'activity, non quando
                elimino un item.

                */
                ordinaOraBtn.setEnabled(false);

                if(start)
                for(DataSnapshot dsKart : snapshot.getChildren()){
                    String[] separated = dsKart.child("codice").getValue().toString().split(Pattern.quote("._*_."));
                    myrefMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshotMenu) {

                            for(DataSnapshot dsMenu : snapshotMenu.getChildren()) {

                                if(dsMenu.child("itemKey").getValue().toString().equals(separated[1])){

                                    ordinaOraBtn.setEnabled(true);

                                    float prezzo = (Float.parseFloat(dsMenu.child("prezzoItem").getValue().toString()))*(Integer.parseInt(dsKart.child("numero").getValue().toString()));
                                    Item item = new Item(dsMenu.child("nomeItem").getValue().toString(), dsMenu.child("immagine").getValue().toString(), String.valueOf(prezzo), "x"+dsKart.child("numero").getValue().toString(), false, dsKart.getKey(), separated[1]);
                                    list.add(item);

                                    adapter.notifyDataSetChanged();

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    start = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }

    public void showOrdini(){


        recyclerView = findViewById(R.id.rvOrd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);

        adapter = new AdapterKart(this, list);
        recyclerView.setAdapter(adapter);

        myrefOrdini.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(ordini)
                for(DataSnapshot dsOrdini : snapshot.getChildren()){

                    System.out.println("ok");
                        String[] separated = dsOrdini.child("codice").getValue().toString().split(Pattern.quote("._*_."));
                        myrefMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshotMenu) {

                                for(DataSnapshot dsMenu : snapshotMenu.getChildren()) {


                                    if(dsMenu.child("itemKey").getValue().toString().equals(separated[1]) && separated[0].equals(user.getUid())){

                                        float prezzo = (Float.parseFloat(dsMenu.child("prezzoItem").getValue().toString()))*(Integer.parseInt(dsOrdini.child("numero").getValue().toString()));
                                        Item item = new Item(dsMenu.child("nomeItem").getValue().toString(), dsMenu.child("immagine").getValue().toString(), String.valueOf(prezzo), "x"+dsOrdini.child("numero").getValue().toString(), true, dsOrdini.getKey(), separated[1]);
                                        list.add(item);

                                        adapter.notifyDataSetChanged();

                                    }

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