package com.fap.bnotion.findaplace;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FavoriteListRecyclerAdapter extends RecyclerView.Adapter<FavoriteListRecyclerAdapter.ViewHolder> {
    public List<FavList> postList;
    public Context context;
    Activity activity;
    public FavoriteListRecyclerAdapter(List<FavList> post_list, Context context){
        this.postList = post_list;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item_view, parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String usernameData = postList.get(position).getUsername();
        final String titleData = postList.get(position).getTitle();
        final String locationData = postList.get(position).getLocation();
        final String categoryData = postList.get(position).getCategory();
        final String contentData = postList.get(position).getDesc();
        final String priceData = postList.get(position).getPrice();
        final String bathsData = postList.get(position).getNumber_of_baths();
        final String toiletsData = postList.get(position).getNumber_of_toilets();
        final String stateData = postList.get(position).getState();
        final String numberData = postList.get(position).getMobile_number();
        final String emailData = postList.get(position).getEmail();
        final String statusData = postList.get(position).getStatus();
        final String idData = postList.get(position).getUser_id();
        final String image_url = postList.get(position).getTimeStamp();
        final String profile_url = postList.get(position).getProfile_url();
        final String dateString = postList.get(position).getId();

        //setting data for views
        holder.setTitleText(titleData);
        holder.setContentText(contentData);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(activity, HousingDetailActivity.class);
                intent.putExtra("username", usernameData);
                intent.putExtra("title", titleData );
                intent.putExtra("location", locationData);
                intent.putExtra("description",contentData);
                intent.putExtra("price", priceData);
                intent.putExtra("category", categoryData);
                intent.putExtra("baths", bathsData);
                intent.putExtra("toilets", toiletsData);
                intent.putExtra("state", stateData);
                intent.putExtra("phoneNumber", numberData);
                intent.putExtra("email", emailData);
                intent.putExtra("status", statusData);
                intent.putExtra("imageURL", image_url);
                intent.putExtra("profileURL", profile_url);
                intent.putExtra("dateString", dateString);
                intent.putExtra("fabState", "shown");
                intent.putExtra("userID", idData);
                intent.putExtra("showButton","show");
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mView;
        private TextView  mTitle, mContent;
        ItemClickListener itemClickListener;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(this);

        }
        
      
        public void setTitleText(String text){
            mTitle = mView.findViewById(R.id.title);
            mTitle.setText(text);
        }
        public void setContentText(String text){
            mContent = mView.findViewById(R.id.content);
            mContent.setText(text);
        }
       
        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
        public void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener=ic;
        }
    }
}
