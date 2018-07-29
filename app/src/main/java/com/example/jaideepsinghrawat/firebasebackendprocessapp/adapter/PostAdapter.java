package com.example.jaideepsinghrawat.firebasebackendprocessapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jaideepsinghrawat.firebasebackendprocessapp.R;
import com.example.jaideepsinghrawat.firebasebackendprocessapp.model.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {
    List<PostModel> postlist;
    Context context;
    private FirebaseFirestore firebaseFirestore;
    public PostAdapter(Context context, List<PostModel> postlist){
        this.context=context;
        this.postlist=postlist;
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_list_item,viewGroup,false);
        Viewholder vh=new Viewholder(v);
        firebaseFirestore=FirebaseFirestore.getInstance();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder viewholder, final int i) {
        if(postlist!=null && postlist.size()>0) {
            String userId=postlist.get(i).getUserId();
            String postdate=postlist.get(i).getTimestamp().toString();
            viewholder.setDate(postdate);
            String description=postlist.get(i).getDescription();
            viewholder.setDescriptiont(description);
            String postImageUri=postlist.get(i).getImageUri();
            viewholder.setpostImage(postImageUri);
            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        String username=task.getResult().getString("name");
                        String image=task.getResult().getString("imageUri");
                        viewholder.setUsername(username);
                        viewholder.setUserImage(image);

                    }else{
                        Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_LONG);
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView postDate,userName,post_description;
        ImageView postImage;
        CircleImageView userImage;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            postDate=itemView.findViewById(R.id.post_date);
            userName=itemView.findViewById(R.id.post_username);

            postImage=itemView.findViewById(R.id.post_image);
            userImage=itemView.findViewById(R.id.post_userImage);
            post_description=itemView.findViewById(R.id.post_des);


        }
        public void setDescriptiont(String text){
            post_description.setText(text);

        }
        public void setUsername(String text){
            userName.setText(text);

        }
        public void setDate(String text){
            postDate.setText(text);

        }
        public void setpostImage(String image){
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.rectangle);
            Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(postImage);

        }
        public void setUserImage(String image){
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.circel);
            Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(userImage);
        }
    }
}
