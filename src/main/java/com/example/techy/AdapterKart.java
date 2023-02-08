package com.example.techy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.lang.UCharacter;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

public class AdapterKart extends RecyclerView.Adapter<AdapterKart.viewHolder>{

    Context context;
    ArrayList<Item> listItem;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String itemMenuId;

    public AdapterKart(Context context, ArrayList<Item> listItem) {
        this.context = context;
        this.listItem = listItem;

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

    }


    @NonNull
    @Override
    public AdapterKart.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kart_layout, parent, false);
        return new viewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull AdapterKart.viewHolder holder, int position) {

        Item item = listItem.get(position);
        holder.idItem.setText(item.getItemKey());
        holder.nome.setText(item.getNomeItem());
        holder.position.setText(String.valueOf(position));
        holder.numero.setText(item.getNumero());
        holder.prezzo.setText(item.getPrezzoItem()+"€");
        Glide.with(context).load(item.getImmagine()).into(holder.immagine);

        System.out.println();

        if(item.getOrdinato()){
            holder.delete.setVisibility(View.INVISIBLE);
            holder.vota.setVisibility(View.VISIBLE);
        }else{
            holder.vota.setVisibility(View.INVISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        TextView nome, prezzo, idItem, numero, position;
        ImageView immagine;
        FloatingActionButton delete;
        Button vota;

        public viewHolder(@NonNull View itemView) {
            super(itemView);


            nome = itemView.findViewById(R.id.itemLayoutNome);
            prezzo = itemView.findViewById(R.id.itemLayoutPrezzo);
            numero = itemView.findViewById(R.id.itemLayoutNumero);
            position = itemView.findViewById(R.id.positionText);
            immagine = itemView.findViewById(R.id.itemLayoutImmagine);
            idItem = itemView.findViewById(R.id.idItem);
            vota = itemView.findViewById(R.id.votaBtn);

            vota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //definisco la rating bar
                    RatingBar rating = new RatingBar(context);

                    //ottengo l'id del prodotto, conoscendo l'id dell'item Ordine
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ordini").child(idItem.getText().toString()).child("codice");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String[] separated = snapshot.getValue(String.class).split(Pattern.quote("._*_."));
                            itemMenuId = separated[1];
                            setExistingRating(rating);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });


                    //definisco un linear layout
                    LinearLayout linearLayout = new LinearLayout(context);
                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    layout.setMargins(50,0,0,30);


                    rating.setLayoutParams(layout); //assegno alla rating bar la proprietà definita in linearLayout (wrap content)
                    rating.setNumStars(5);
                    rating.setStepSize(0.5F);
                    linearLayout.addView(rating); //aggiungo la rating bar al linear layout

                    AlertDialog.Builder builder = new AlertDialog.Builder(context); //definisco il dialog box
                    builder.setTitle(nome.getText().toString());
                    builder.setMessage("La tua opinione è importante per noi");
                    builder.setView(linearLayout); //aggiungo la dialog box al linearlayout

                    builder.setPositiveButton("Conferma",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    addRating(itemMenuId, rating.getRating());

                                    dialog.dismiss();
                                }

                    }).setNegativeButton("Annulla",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    builder.create();
                    builder.show();

                }
            });

            delete = (FloatingActionButton)itemView.findViewById(R.id.floatingActionButton);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatabaseReference refFav = database.getReference("Carrello").child(idItem.getText().toString());
                    refFav.removeValue();

                    Ordinazioni.list.remove(Integer.parseInt(position.getText().toString()));
                    notifyDataSetChanged();

                }
            });



        }



        private void setExistingRating(RatingBar rating){


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ratings").child(itemMenuId).child(auth.getUid());

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {
                        float voto = Float.parseFloat(String.valueOf(snapshot.getValue()));
                        rating.setRating(voto);
                    }catch (Exception nullException){

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }



        private void addRating(String itemId, float rate){

            DatabaseReference refRatings = database.getReference("Ratings").child(itemId).child(auth.getUid());

            refRatings.setValue(rate);





        }

    }

}
