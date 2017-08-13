package com.pritz.sikkimuniversity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.pritz.sikkimuniversity.R.id.pictures;


public class Mainnotice extends Fragment {

    private RecyclerView postinsta;
    private DatabaseReference mdatabase;
    ProgressBar progressBar1;
    public Mainnotice() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mainnotice, container, false);
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar1=(ProgressBar)getActivity().findViewById(R.id.progressBar);
        progressBar1.setVisibility(View.VISIBLE);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Importantnotifications");
        postinsta=(RecyclerView)getActivity().findViewById(R.id.posti);
        postinsta.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        // recyclerView.setLayoutManager(mLayoutManager);
        mdatabase.keepSynced(true);
        postinsta.setLayoutManager(mLayoutManager);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<imp, Notificaion.BlogViewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<imp, Notificaion.BlogViewholder>(
                imp.class,R.layout.posti, Notificaion.BlogViewholder.class,mdatabase.limitToLast(8)) {
            @Override
            protected void populateViewHolder(Notificaion.BlogViewholder viewHolder, imp model, int position) {
                final String pskey=getRef(position).getKey();
                viewHolder.setname(model.getname());
                viewHolder.setImage(getActivity().getApplicationContext(),model.getImage());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setDate(model.getDate());
                progressBar1.setVisibility(View.GONE);
               viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_LONG).show();
                        mdatabase.child(pskey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                 Intent intent = new Intent(getActivity(),Important.class);
                           intent.putExtra("gte",pskey);
                                startActivity(intent);
                             }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }
        };
        postinsta.setAdapter(firebaseRecyclerAdapter);
        mdatabase.keepSynced(true);
    }




    public static class BlogViewholder extends RecyclerView.ViewHolder{

        View view;
        public BlogViewholder(View itemView) {
            super(itemView);
            view=itemView;

        }
        public void setname(String name)
        {
            TextView ptitle=(TextView)view.findViewById(R.id.name);
            ptitle.setText(name);
        }
        public void setMessage(String message)
        {
            TextView pdetail=(TextView)view.findViewById(R.id.mainframe);
            pdetail.setText(message);
        }

        public void setImage(Context ctx, String image)
        {
            ImageView post=(ImageView)view.findViewById(pictures);
            Picasso.with(ctx).load(image).into(post);

        }

        public void setDate(String date)
        {
            TextView date1=(TextView)view.findViewById(R.id.date);
            date1.setText(date);
        }
    }
}

