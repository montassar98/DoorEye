package com.montassarselmi.dooreye.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.R;

import java.util.ArrayList;

public class FamilyRecyclerViewAdapter extends RecyclerSwipeAdapter<FamilyRecyclerViewAdapter.SimpleViewHolder> {

    private Context mContext;
    private ArrayList<User> usersList;
    private static final String TAG = FamilyRecyclerViewAdapter.class.getSimpleName();

    public FamilyRecyclerViewAdapter(Context context, ArrayList<User> objects) {
        this.mContext = context;
        this.usersList = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family_recyclerview, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder simpleViewHolder, final int i) {
        final User item = usersList.get(i);
        simpleViewHolder.txtUserName.setText(item.getFullName());
        simpleViewHolder.txtUserPhone.setText(item.getPhoneNumber());
        simpleViewHolder.txtUserEmail.setText(item.getEmail());
        if (item.getStatus()!= null && item.getStatus().equals("admin"))
        {
            simpleViewHolder.txtAdmin.setVisibility(View.VISIBLE);
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
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Clicked on Edit  " + simpleViewHolder.txtUserName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        simpleViewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(simpleViewHolder.swipeLayout);
                usersList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, usersList.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + simpleViewHolder.txtUserName.getText().toString(), Toast.LENGTH_SHORT).show();
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
        TextView txtUserName;
        TextView txtUserPhone;
        TextView txtUserEmail;
        TextView tvDelete;
        TextView tvEdit;
        TextView txtAdmin;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_user_name);
            txtUserPhone = (TextView) itemView.findViewById(R.id.txt_user_phone);
            txtUserEmail = (TextView) itemView.findViewById(R.id.txt_user_email);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            txtAdmin = (TextView) itemView.findViewById(R.id.txt_admin);

        }
    }
}
