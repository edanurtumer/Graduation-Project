package com.proje.steganography;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayush.steganography.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private DatabaseReference friendRequestReference;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;
    private String mCurrentUser;

    //Views
    private RecyclerView recyclerViewAccept;
    private View mRequestView;

    public RequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRequestView = inflater.inflate(R.layout.fragment_requests, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //RecyclerView

        recyclerViewAccept = (RecyclerView) mRequestView.findViewById(R.id.request_fragment_recycler_view_accept);
        recyclerViewAccept.setLayoutManager(layoutManager);
        recyclerViewAccept.setHasFixedSize(true);
        //Creating Database refrence
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = currentUser.getUid();
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUser);
        friendRequestReference.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);

        return mRequestView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Accept, RequestsFragment.AcceptViewHolder> acceptAdapter =
                new FirebaseRecyclerAdapter<Accept, RequestsFragment.AcceptViewHolder>(
                        Accept.class,
                        R.layout.request_single_layout,
                        RequestsFragment.AcceptViewHolder.class,
                        friendRequestReference
                ) {
                    @Override
                    protected void populateViewHolder(final RequestsFragment.AcceptViewHolder viewHolder, Accept model, int position) {

                        String requestType = model.getRequest_type();

                        final String uid = getRef(position).getKey().toString();
                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child(uid).child("name").getValue(String.class);
                                String image = dataSnapshot.child(uid).child("thumb_image").getValue().toString();
                                String status = dataSnapshot.child(uid).child("status").getValue().toString();
                                viewHolder.setStatus(status);
                                viewHolder.setName(name);
                                viewHolder.setImage(image);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };
        recyclerViewAccept.setAdapter(acceptAdapter);
    }

    //View Holder class for Accept Request
    public static class AcceptViewHolder extends RecyclerView.ViewHolder {
        View acceptView;
        Button acceptButton;
        Button declineButton;
        public AcceptViewHolder(View itemView) {
            super(itemView);
            acceptView = itemView;
            declineButton = acceptView.findViewById(R.id.request_fragment_button_decline);
            acceptButton = acceptView.findViewById(R.id.request_fragment_button);
        }
        public void setButtons(String type) {
            declineButton.setVisibility(View.INVISIBLE);
            acceptButton.setText("Cancel");
        }
        public void setStatus(final String status) {
            TextView mStatus = acceptView.findViewById(R.id.request_fragment_status);
            mStatus.setText(status);
        }
        public void setName(final String name) {
            TextView acceptName = acceptView.findViewById(R.id.request_fragment_name);
            acceptName.setText(name);
        }
        public void setImage(final String image) {
            CircleImageView mImage = (CircleImageView) acceptView.findViewById(R.id.request_fragment_image);
            //picasso image downloading and
            //Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mImage);
        }
    }
}

