package com.montassarselmi.dooreye.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.montassarselmi.dooreye.Model.EventHistory;
import com.montassarselmi.dooreye.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RecyclerViewAllHistoryAdapter extends RecyclerView.Adapter<RecyclerViewAllHistoryAdapter.mViewHolder> {

    private Context mContext;
    private ArrayList<EventHistory> eventHistories;
    private static final String TAG = FamilyRecyclerViewAdapter.class.getSimpleName();

    public RecyclerViewAllHistoryAdapter(Context mContext, ArrayList<EventHistory> eventHistories)
    {
        this.mContext = mContext;
        this.eventHistories = eventHistories;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_history,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewHolder holder, final int position) {
        final EventHistory item = eventHistories.get(position);
        int icon = item.getIcon();
        holder.imgState.setImageDrawable(mContext.getDrawable(item.getIcon()));
        holder.rlExpand.setVisibility(View.GONE);
        holder.txtTime.setText(item.getEventTime());
        if (item.getResponder() != null) {
            holder.txtResponder.setText(item.getResponder());
            holder.txtDash.setVisibility(View.VISIBLE);
        }
        else {
            holder.txtResponder.setText("");
            holder.txtDash.setVisibility(View.GONE);
        }
        holder.txtState.setText(item.getStatus());
        if (item.getVisitorImage() != null)
            Picasso.get().load(item.getVisitorImage()).into(holder.imgVisitor);
        if (item.getStatus().equals("Door Check"))
            holder.imgExpand.setVisibility(View.GONE);
        else holder.imgExpand.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ""+position, Toast.LENGTH_SHORT).show();
                if (!item.getStatus().equals("Door Check"))
                {
                    if (holder.rlExpand.getVisibility() == View.GONE) {
                        holder.rlExpand.setVisibility(View.VISIBLE);
                        holder.imgExpand.setVisibility(View.GONE);
                        holder.imgExpandOff.setVisibility(View.VISIBLE);
                    }else {
                        holder.rlExpand.setVisibility(View.GONE);
                        holder.imgExpand.setVisibility(View.VISIBLE);
                        holder.imgExpandOff.setVisibility(View.GONE);

                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventHistories.size();
    }


    public static class mViewHolder extends RecyclerView.ViewHolder{

        ImageView imgState;
        ImageView imgExpand;
        ImageView imgExpandOff;
        TextView txtTime;
        TextView txtState;
        TextView txtResponder;
        TextView txtDash;
        RelativeLayout rlExpand;
        ImageView imgVisitor;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            imgState = itemView.findViewById(R.id.img_state);
            imgExpand = itemView.findViewById(R.id.img_expand);
            imgExpandOff = itemView.findViewById(R.id.img_expand_off);
            txtTime = itemView.findViewById(R.id.txt_time);
            txtState = itemView.findViewById(R.id.txt_state);
            txtResponder = itemView.findViewById(R.id.txt_responder);
            txtDash = itemView.findViewById(R.id.dash);
            rlExpand = itemView.findViewById(R.id.rl_expand);
            imgVisitor = itemView.findViewById(R.id.img_visitor);
        }
    }
}
