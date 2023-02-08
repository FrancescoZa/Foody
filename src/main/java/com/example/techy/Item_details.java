package com.example.techy;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Item_details extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myref = database.getReference("Menu");
    DatabaseReference myrefOrdini = database.getReference("Ordini");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView nomeTxtv, descrizioneTxtv, prezzoTxtv, ratingTxt;
    ImageView imageview;
    Button increaseBtn, decreaseBtn, backBtn, favBtn, addToKartBtn, orderNowBtn;
    EditText number;

    RatingBar rating;

    String nomeItem;
    String prezzoItem;
    String immagineItem;
    String descrizioneItem;
    float votoItem;

    Boolean fav = false;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        getSupportActionBar().hide();

        extras = getIntent().getExtras();


        calculateAverage(extras.getString("itemKey"));

        checkIfFav();

        nomeTxtv = findViewById(R.id.nomeTxtv);
        descrizioneTxtv = findViewById(R.id.descrizioneTxtv);
        prezzoTxtv = findViewById(R.id.prezzoTxtv);
        imageview = findViewById(R.id.imageView);
        increaseBtn = findViewById(R.id.IncreaseBtn);
        decreaseBtn = findViewById(R.id.decreaseBtn);
        number = findViewById(R.id.number);
        backBtn = findViewById(R.id.backBtn);
        favBtn = findViewById(R.id.favBtn);
        addToKartBtn = findViewById(R.id.addToKartBtn);
        orderNowBtn = findViewById(R.id.orderNowBtn);
        rating = findViewById(R.id.ratingBar);
        ratingTxt = findViewById(R.id.ratingTxt);

        rating.setRating(0);
        ratingTxt.setText("0.0");

        orderNowBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(Item_details.this);

                alert.setTitle("Tavolo");
                alert.setMessage("Inserisci il numero che trovi sul tavolo");

                LinearLayout layout = new LinearLayout(Item_details.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(300, 20, 300, 50);

                EditText numTxt = new EditText(Item_details.this);
                numTxt.setGravity(Gravity.CENTER); //centrare il testo
                numTxt.setInputType(InputType.TYPE_CLASS_NUMBER);

                layout.addView(numTxt, params);

                alert.setView(layout);

                alert.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        orderNow(numTxt.getText().toString());
                    }
                });

                alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();

            }
        });

        addToKartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addToKart();

            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fav == false){

                    favBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_40, 0);
                    fav = true;
                    addToFavourite();
                }else{
                    favBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_border_24, 0);
                    fav = false;
                    removeFromFavourite();
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int n = parseInt(number.getText().toString());

                n = n+1;
                number.setText(String.valueOf(n));
                float newPrezzo = parseFloat(prezzoItem)*n;
                prezzoTxtv.setText(String.valueOf(newPrezzo) + "€");


            }
        });

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int n = parseInt(number.getText().toString());
                if(n >= 2){
                    n = n-1;
                    number.setText(String.valueOf(n));
                    float newPrezzo = parseFloat(prezzoItem)*n;
                    prezzoTxtv.setText(String.valueOf(newPrezzo) + "€");
                }

            }
        });

        DatabaseReference finalRef = myref.child(extras.getString("itemKey"));
        finalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);

                nomeItem = dataSnapshot.child("nomeItem").getValue().toString();
                prezzoItem = dataSnapshot.child("prezzoItem").getValue().toString();
                immagineItem = dataSnapshot.child("immagine").getValue().toString();
                descrizioneItem = dataSnapshot.child("descrizioneItem").getValue().toString();
                nomeTxtv.setText(nomeItem);
                prezzoTxtv.setText(prezzoItem + "€");
                descrizioneTxtv.setText(descrizioneItem);
                Glide.with(Item_details.this).load(immagineItem).into(imageview);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void orderNow(String codiceTavolo){
        HashMap<String, String> val = new HashMap<String, String>();
        val.put("numero", number.getText().toString());
        val.put("codice", user.getUid()+"._*_."+extras.getString("itemKey"));
        val.put("codiceTavolo", codiceTavolo);

        myrefOrdini.push().setValue(val);

        Toast.makeText(Item_details.this, "Ordine avvenuto con successo", Toast.LENGTH_SHORT).show();
    }

    private  void addToKart(){

        //aggiungo un child avente come id: keyUser_keyItem

        String numberItems = number.getText().toString();
        HashMap<String, String> val = new HashMap<String, String>();
        val.put("numero", numberItems);
        val.put("codice", user.getUid()+"._*_."+extras.getString("itemKey"));

        DatabaseReference refFav = database.getReference("Carrello");
        refFav.push().setValue(val);

        Toast.makeText(Item_details.this, "Aggiunto al carrello, potrai ordinarlo successivamente.", Toast.LENGTH_SHORT).show();

    }

    private void addToFavourite(){

        //aggiungo un child avente come id: keyUser_keyItem

        DatabaseReference refFav = database.getReference("Favourite").child(user.getUid()+"=_*_="+extras.getString("itemKey"));
        refFav.push().setValue("");
        Toast.makeText(Item_details.this, "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show();

    }

    private void removeFromFavourite(){
        //rimuovo il child avente come id: keyUser_keyItem

        DatabaseReference refFav = database.getReference("Favourite").child(user.getUid()+"=_*_="+extras.getString("itemKey"));
        refFav.removeValue();
        Toast.makeText(Item_details.this, "Rimosso dai preferiti", Toast.LENGTH_SHORT).show();

    }

    private void checkIfFav(){
        DatabaseReference refFav = database.getReference("Favourite");

        refFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(user.getUid()+"=_*_="+extras.getString("itemKey"))) {
                    fav = true;
                    favBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_40, 0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    Float somma, average;
    private void calculateAverage(String itemId){

        average = Float.valueOf(0);
        somma =  Float.valueOf(0);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ratings").child(itemId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.getChildrenCount() > 0) { //ovviamente snapshot.getChildrenCount() deve essere >0 per non dividere per 0

                    for(DataSnapshot ds : snapshot.getChildren()) {

                        float voto = Float.parseFloat(String.valueOf(ds.getValue()));
                        somma = somma+voto;

                    }

                    average = somma/snapshot.getChildrenCount();
                    System.out.println(average + " " + somma);

                    rating.setRating(average);
                    ratingTxt.setText(String.valueOf(average).substring(0,3));


                }else{
                    rating.setRating(0);
                    ratingTxt.setText("0.0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}