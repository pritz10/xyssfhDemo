package com.pritz.sikkimuniversity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.media.MediaPlayer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;
import static android.R.id.message;

public class Lost_Found extends AppCompatActivity {

    ImageButton lost;
    EditText ltitle ;
    EditText ldetail ;
    private  Uri imageurl=null;
    private static final int GALLERY_REQUEST=1;
    private StorageReference mStorageRef;
    DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("lost");

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost__found);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStorageRef=FirebaseStorage.getInstance().getReference();

        //initialization//
        lost = (ImageButton) findViewById(R.id.lost);
        ltitle = (EditText) findViewById(R.id.ltitle);
        ldetail = (EditText) findViewById(R.id.ldetail);
        progressDialog=new ProgressDialog(this);
        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_REQUEST);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startposting();
            }
        });
    }
    private void startposting() {
        progressDialog.setMessage("Just Wait.....");
        final MediaPlayer mp=MediaPlayer.create(this,R.raw.lo);
        final String title = ltitle.getText().toString().trim();
        final String detail = ldetail.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String name = sharedPreferences.getString("s_name","");
        final String Department= sharedPreferences.getString("s_dept","");

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(detail) && imageurl != null)
        {
            progressDialog.show();
            StorageReference reference=mStorageRef.child("Blog_images").child(imageurl.getLastPathSegment());
            reference.putFile(imageurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    Uri downloaduri = taskSnapshot.getDownloadUrl();

                    DatabaseReference databaseReference = mref.push();
                    databaseReference.child("title").setValue(title);
                    databaseReference.child("detail").setValue(detail);
                    databaseReference.child("image").setValue(downloaduri.toString());


                    SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    String name = sharedPreferences.getString("s_name", "");

                    databaseReference.child("Username").setValue(name);
                    final String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
                    databaseReference.child("date").setValue(currentDateTimeString);
                    progressDialog.dismiss();
                    mp.start();
                    Intent i = new Intent(Lost_Found.this, LostFound.class);
                    finish();
                    startActivity(i);


                }
            });


        }
    }

    @Override
    public  void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            imageurl=data.getData();
            lost.setImageURI(imageurl);
        }
    }

}