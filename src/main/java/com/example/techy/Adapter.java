package com.example.techy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {

    Context context;
    ArrayList<Item> listItem;

    public Adapter(Context context, ArrayList<Item> listItem) {
        this.context = context;
        this.listItem = listItem;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Item item = listItem.get(position);
        holder.idItem.setText(item.getItemKey());
        holder.nome.setText(item.getNomeItem());
        holder.position.setText(String.valueOf(position));
        holder.prezzo.setText(item.getPrezzoItem()+"€");
        Glide.with(context).load(item.getImmagine()).into(holder.immagine);
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView nome, prezzo, idItem, position;
        ImageView immagine;
        FloatingActionButton open;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.itemLayoutNome);
            prezzo = itemView.findViewById(R.id.itemLayoutPrezzo);
            immagine = itemView.findViewById(R.id.itemLayoutImmagine);
            idItem = itemView.findViewById(R.id.idItem);
            position = itemView.findViewById(R.id.positionLayout);

            open = (FloatingActionButton)itemView.findViewById(R.id.floatingActionButton);
            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    System.out.println();
                    Intent myIntent = new Intent(view.getContext(),Item_details.class);
                    myIntent.putExtra("itemKey", idItem.getText().toString());
                    view.getContext().startActivity(myIntent);
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {

                    if(Home.isAdmin){

                        //riferimento alert dialog https://developer.android.com/develop/ui/views/components/dialogs

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setTitle("Attenzione");
                        builder.setMessage("Vuoi rimuovere questo prodotto?");

                        builder.setPositiveButton("SÌ", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                delete();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss(); //non fa niente
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    }
                    return true;
                }

            });



        }

        private void delete(){
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference refMenu = database.getReference("Menu").child(idItem.getText().toString());
            refMenu.removeValue();

            Home.list.remove(Integer.parseInt(position.getText().toString()));
            notifyDataSetChanged();

        }


    }
}
