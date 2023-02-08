package com.example.techy;

import static java.lang.Float.parseFloat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class addNewItem extends AppCompatActivity {

    ImageView itemImage;
    TextView itemName, itemDescrizione, itemPrezzo;
    Spinner itemCategory;
    ProgressBar progress;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String category;
    private Uri ImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        itemImage = (ImageView)findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        Spinner itemCategory = (Spinner) findViewById(R.id.itemCategory);
        itemDescrizione = findViewById(R.id.itemDescrizione);
        itemPrezzo = findViewById(R.id.itemLayoutPrezzo);
        Button addButton = findViewById(R.id.addBtn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategory.setAdapter(adapter);

        category = itemCategory.getSelectedItem().toString();


        itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = itemCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!itemName.getText().toString().isEmpty() && ImgUri != null){

                    insertToDB();

                }else{
                    Toast.makeText(addNewItem.this, "Devi riempire tutti i campi", Toast.LENGTH_SHORT).show();
                }

            }
        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

    }

    private void insertToDB(){

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Upload item");
        progress.setMessage("Attendi...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Immagini");
        StorageReference percorsoImmagine = storageRef.child(ImgUri.getLastPathSegment());

        percorsoImmagine.putFile(ImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                percorsoImmagine.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String downloadLinkImmagine = uri.toString();
                        float prezzo = parseFloat(itemPrezzo.getText().toString());
                        Item item = new Item(itemName.getText().toString(), category, downloadLinkImmagine, itemDescrizione.getText().toString(), String.valueOf(prezzo));

                        addItem(item, progress);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progress.dismiss();
                        Toast.makeText(addNewItem.this, "Impossibile caricare l'item nel database.", Toast.LENGTH_SHORT).show();


                    }
                });

            }
        });

    }


    private void addItem(Item item, ProgressDialog progress){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Menu").push();

        //genera la chiave dell'item
        String key = reference.getKey();
        item.setItemKey(key);

        reference.setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(addNewItem.this, "Item caricato correttamente", Toast.LENGTH_SHORT).show();
                progress.dismiss();

            }
        });

    }

    private void openGallery(){

        //creo un intent per la galleria, aspetto che l'utente selezioni una immagine

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int REQUESTCODE = 2;

        if(resultCode == RESULT_OK && requestCode == REQUESTCODE && data!= null){

            ImgUri = data.getData();    //immagine selezionata
            itemImage.setImageURI(ImgUri);

        }

    }
}

