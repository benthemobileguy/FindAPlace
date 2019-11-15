package com.fap.bnotion.findaplace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgentPostItemRecyclerAdapter extends RecyclerView.Adapter<AgentPostItemRecyclerAdapter.ViewHolder> {
    public List<AgentPost> postList;
    public Context context;
    Activity activity;
    public AgentPostItemRecyclerAdapter(List<AgentPost> post_list, Context context){
        this.postList = post_list;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_dashboard_post, parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final AgentPost agentPost = postList.get(position);
        final String blogPostID = postList.get(position).BlogPostId;
        final String usernameData = postList.get(position).getUsername();
        final String titleData = postList.get(position).getTitle();
        final String locationData = postList.get(position).getLocation();
        final String categoryData = postList.get(position).getCategory();
        final String descData = postList.get(position).getDesc();
        final String priceData = postList.get(position).getPrice();
        final String bathsData = postList.get(position).getNumber_of_baths();
        final String toiletsData = postList.get(position).getNumber_of_toilets();
        final String stateData = postList.get(position).getState();
        final String numberData = postList.get(position).getMobile_number();
        final String emailData = postList.get(position).getEmail();
        final String statusData = postList.get(position).getStatus();
        final String idData = postList.get(position).getUser_id();
        final String areaData = postList.get(position).getArea();
        final String price_rangeData = postList.get(position).getPrice_range();
        holder.setTitleText(titleData + " (" + categoryData + ")");
        //get Post Image
        final String image_url = postList.get(position).getImage_url();
        final ArrayList<String> image_urls = postList.get(position).getImage_urls();
        //get Profile Image
        final String profile_url = postList.get(position).getProfile_url();
        long milliseconds = postList.get(position).getTimestamp().getTime();
        final String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTimeText(dateString + " (date posted)");
        holder.mDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewPostActivity.class);
                intent.putExtra("username", usernameData);
                intent.putExtra("title", titleData );
                intent.putExtra("location", locationData);
                intent.putExtra("description", descData);
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
                intent.putExtra("area", areaData);
                intent.putExtra("userID", idData);
                intent.putExtra("price_range", price_rangeData);
                intent.putExtra("data", "data");
                intent.putExtra("imageURLS", image_urls);
                intent.putExtra("agentPost", agentPost);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                activity.finish();

            }
        });
        holder.mEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewPostActivity.class);
                intent.putExtra("username", usernameData);
                intent.putExtra("title", titleData );
                intent.putExtra("location", locationData);
                intent.putExtra("description", descData);
                intent.putExtra("price", priceData);
                intent.putExtra("category", categoryData);
                intent.putExtra("baths", bathsData);
                intent.putExtra("toilets", toiletsData);
                intent.putExtra("state", stateData);
                intent.putExtra("area", areaData);
                intent.putExtra("phoneNumber", numberData);
                intent.putExtra("email", emailData);
                intent.putExtra("status", statusData);
                intent.putExtra("imageURL", image_url);
                intent.putExtra("imageURLS", image_urls);
                intent.putExtra("profileURL", profile_url);
                intent.putExtra("dateString", dateString);
                intent.putExtra("price_range", price_rangeData);
                intent.putExtra("userID", idData);
                intent.putExtra("data", "data");
                intent.putExtra("agentPost", agentPost);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                activity.finish();
            }
        });
holder.setItemClickListener(new ItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(activity, NewPostActivity.class);
        intent.putExtra("username", usernameData);
        intent.putExtra("title", titleData );
        intent.putExtra("location", locationData);
        intent.putExtra("description", descData);
        intent.putExtra("price", priceData);
        intent.putExtra("category", categoryData);
        intent.putExtra("baths", bathsData);
        intent.putExtra("toilets", toiletsData);
        intent.putExtra("state", stateData);
        intent.putExtra("area", areaData);
        intent.putExtra("phoneNumber", numberData);
        intent.putExtra("email", emailData);
        intent.putExtra("status", statusData);
        intent.putExtra("imageURL", image_url);
        intent.putExtra("profileURL", profile_url);
        intent.putExtra("dateString", dateString);
        intent.putExtra("imageURLS", image_urls);
        intent.putExtra("price_range", price_rangeData);
        intent.putExtra("userID", idData);
        intent.putExtra("data", "data");
        intent.putExtra("agentPost", agentPost);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        activity.finish();
    }
});

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View mView;
        private TextView mTitle, mDate;
        private ImageView mEditPost, mDeletePost;
        ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mEditPost = mView.findViewById(R.id.edit_post);
            mDeletePost = mView.findViewById(R.id.delete_post);
            itemView.setOnClickListener(this);
            mEditPost.setOnClickListener(this);
        }

        public void setTitleText(String text) {
            mTitle = mView.findViewById(R.id.title);
            mTitle.setText(text);
        }

        public void setTimeText(String date) {
            mDate = mView.findViewById(R.id.date);
            mDate.setText(date);
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
