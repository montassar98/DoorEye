package com.montassarselmi.dooreye.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.montassarselmi.dooreye.Model.User;
import com.montassarselmi.dooreye.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsRVAdapter extends RecyclerView.Adapter<RequestsRVAdapter.RVHolder> {

    public interface onRequestClickListener{
        void onAccept(View view, int position);
        void onDecline(View view, int position);
    }
    public void setClickListener(onRequestClickListener itemClickListener)
    {
        this.onRequestClickListener=itemClickListener;
    }

    private onRequestClickListener onRequestClickListener;
    private LayoutInflater inflater;

    private ArrayList<User> mDataSet;
    public RequestsRVAdapter(@NonNull Context context, ArrayList<User> mDataSet) {

        this.inflater=LayoutInflater.from(context);
        this.mDataSet = mDataSet;
        //onRequestClickListener = listener;
    }


    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.item_request,parent,false);


        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {

        holder.txtUserName.setText(mDataSet.get(position).getFullName());
        if (mDataSet.get(position).getProfileImage() != null){
            Picasso.get().load(mDataSet.get(position).getProfileImage()).into(holder.imgProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public  class RVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtUserName;
        Button btnAccept, btnDecline;
        ImageView imgProfile;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txt_user_name_request);
            btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
            btnDecline = (Button) itemView.findViewById(R.id.btn_decline);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.img_profile);
            btnAccept.setOnClickListener(this);
            btnDecline.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRequestClickListener != null){
                if (v.getId() == R.id.btn_accept)
                    onRequestClickListener.onAccept(v,getAdapterPosition());
                if (v.getId() == R.id.btn_decline)
                    onRequestClickListener.onDecline(v,getAdapterPosition());
            }
        }
    }
}
