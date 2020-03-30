package com.montassarselmi.dooreye.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.montassarselmi.dooreye.EditActivity;
import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.R;

import java.io.IOException;
import java.util.ArrayList;
import com.montassarselmi.dooreye.MainActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyRecyclerViewAdapter extends RecyclerSwipeAdapter<FamilyRecyclerViewAdapter.SimpleViewHolder> {



    private Context mContext;
    private ArrayList<User> usersList;
    private static final String TAG = FamilyRecyclerViewAdapter.class.getSimpleName();
    private FirebaseAuth mAuth;
    private String pNumber;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRefUser=database.getReference();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String boxId;

    public FamilyRecyclerViewAdapter(Context context, ArrayList<User> objects) {
        this.mContext = context;
        this.usersList = objects;

    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mSharedPreferences = mContext.getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family_recyclerview, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder simpleViewHolder, final int i) {
        boxId = mSharedPreferences.getString("BOX_ID","Null");
        pNumber=mAuth.getCurrentUser().getPhoneNumber();
        final User item = usersList.get(i);
        if (item.getProfileImage() != null)
        {
            Picasso.get().load(item.getProfileImage()).into(simpleViewHolder.imgProfileImage);
        }
        simpleViewHolder.txtUserName.setText(item.getFullName());
        simpleViewHolder.txtUserPhone.setText(item.getPhoneNumber());
        simpleViewHolder.txtUserEmail.setText(item.getEmail());

        if (item.getStatus()!= null && item.getStatus().equals("admin"))
        {
            simpleViewHolder.txtAdmin.setVisibility(View.VISIBLE);
        }
        if (item.getPhoneNumber().equals(pNumber))
        {
            simpleViewHolder.txtCurrentUser.setVisibility(View.VISIBLE);
        }

        simpleViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //drag from the left
        simpleViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,simpleViewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        // Drag From Right
        simpleViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, simpleViewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));

        simpleViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.d(TAG, "onUpdateSwipe you are swiping ");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        simpleViewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "onClick: "+item.getFullName(), Toast.LENGTH_SHORT).show();
            }
        });



        simpleViewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d(TAG, "onClick: Edit");
                mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("BoxList").child(boxId).child("users")
                                .child(mAuth.getUid()).child("phoneNumber").getValue().equals(item.getPhoneNumber())) {
                            Intent intent = new Intent(mContext, EditActivity.class);
                            intent.putExtra("EMAIL",item.getEmail());
                            intent.putExtra("FULL_NAME",item.getFullName());
                            Log.d(TAG, "onDataChange: hello");
                            view.getContext().startActivity(intent);
                        }else
                            Toast.makeText(mContext, mContext.getResources().getText(R.string.you_cant_modify), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        simpleViewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("BoxList").child(boxId).child("users").child(mAuth.getUid()).child("status").getValue().toString().equals("admin")) {
                            mItemManger.removeShownLayouts(simpleViewHolder.swipeLayout);
                            usersList.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, usersList.size());
                            mItemManger.closeAllItems();
                            Query mUserQuery = mRefUser.child("BoxList").child(boxId).child("users").orderByChild("fullName").equalTo(simpleViewHolder.txtUserName.getText().toString());
                            mUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                        Toast.makeText(mContext, "Deleted " + simpleViewHolder.txtUserName.getText().toString(), Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    }else Toast.makeText(mContext, mContext.getResources().getText(R.string.you_cant_delete), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(simpleViewHolder.itemView, i);
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView imgProfileImage;
        TextView txtUserName;
        TextView txtUserPhone;
        TextView txtUserEmail;
        TextView tvDelete;
        TextView tvEdit;
        TextView txtAdmin;
        TextView txtCurrentUser;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            imgProfileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_user_name);
            txtUserPhone = (TextView) itemView.findViewById(R.id.txt_user_phone);
            txtUserEmail = (TextView) itemView.findViewById(R.id.txt_user_email);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            txtAdmin = (TextView) itemView.findViewById(R.id.txt_admin);
            txtCurrentUser=(TextView)itemView.findViewById(R.id.txt_current_user);


        }
    }

}
