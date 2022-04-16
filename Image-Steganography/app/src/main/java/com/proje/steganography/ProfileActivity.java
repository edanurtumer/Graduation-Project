package com.proje.steganography;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ayush.steganography.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrent_user;

    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (ImageView) findViewById(R.id.profil_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profil_send_reg_btn);
        mDeclineBtn = (Button) findViewById(R.id.profil_dec_reg_btn);

        mCurrent_state = "not_friends";

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Kullanıcı Verilerini Yüklüyor.");
        mProgressDialog.setMessage("Lütfen kullanıcı verilerini yüklerken bekleyin.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue(String.class);
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("profil_images").getValue(String.class);

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                mFriendsReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Arkadaşlığı Kabul Et");


                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                            }else if(req_type.equals("sent")){

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Arkadaşlık İsteğini İptal Et");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }

                            mProgressDialog.dismiss();

                        }else {

                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Bu Kişiyi Kaldır");


                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                if(mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificaitonId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrent_user.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificaitonId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null) {

                                Toast.makeText(ProfileActivity.this, "There was some erro in sending request", Toast.LENGTH_SHORT).show();

                            }

                            mProfileSendReqBtn.setEnabled(true);

                            mCurrent_state = "req_sent";
                            mProfileSendReqBtn.setText("Arkadaşlığı İptal Et");

                        }
                    });


                }



                if(mCurrent_state.equals("req_sent")){

                    mFriendsReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Arkadaşlık İsteği Gönder");


                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);

                                }
                            });

                        }
                    });

                }




                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);

                    friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "friends";
                                mProfileSendReqBtn.setText("Arkadaşlıktan Kaldır");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

                if(mCurrent_state.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Arkadaşa İstek Gönder");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                            }

                            mProfileSendReqBtn.setEnabled(true);
                        }

                    });

                }


            }

        });

    }
}
