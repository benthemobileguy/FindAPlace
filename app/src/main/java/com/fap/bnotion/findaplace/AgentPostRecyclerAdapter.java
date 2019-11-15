package com.fap.bnotion.findaplace;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;

public class AgentPostRecyclerAdapter extends RecyclerView.Adapter<AgentPostRecyclerAdapter.ViewHolder> {
    public List<AgentPost> postList;
    public Context context;
    Activity activity;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private String user_id;
    public AgentPostRecyclerAdapter(List<AgentPost> post_list, Context context){
        this.postList = post_list;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_post, parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String blogPostID = postList.get(position).BlogPostId;
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final String uniqueID = UUID.randomUUID().toString();
        if(firebaseUser!=null){
            user_id = firebaseAuth.getCurrentUser().getUid();
        }
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
        holder.setDescriptionText(descData);
        holder.setLocationText(locationData);
        if (categoryData.equals("Handyman")) {
            holder.setPriceText("");
        } else{
            holder.setPriceText("₦" +  priceData);
        }
        holder.setUsernameText(usernameData);
        holder.setTitleText(titleData + " (" + categoryData + ")");
        //set Post Image
        final String image_url = postList.get(position).getImage_url();
        final ArrayList<String> image_urls = postList.get(position).getImage_urls();
        if (image_url.equals("null")) {
            holder.setPostImage(image_urls.get(0));
        } else {
            holder.setPostImage(image_url);
        }

        //setProfile Image
        final String profile_url = postList.get(position).getProfile_url();
        holder.setProfileImage(profile_url);
        long milliseconds = postList.get(position).getTimestamp().getTime();
        final String dateString = DateFormat.format("MMM dd, yyyy", new Date(milliseconds)).toString();
        holder.setTimeText(dateString);
        //Get Views Count
        if(blogPostID!= null){
            firebaseFirestore.collection("Posts").document(blogPostID).collection("Views").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots,  FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        holder.updateViewsCount(count);
                    } else {
                        holder.updateViewsCount(0);
                    }
                }
            });
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(activity, HousingDetailActivity.class);
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
                intent.putExtra("imageURLS", image_urls);
                intent.putExtra("profileURL", profile_url);
                intent.putExtra("dateString", dateString);
                intent.putExtra("area", areaData);
                intent.putExtra("userID", idData);
                //Views Count
                Map<String, Object> viewsMap = new HashMap<>();
                viewsMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts").document(blogPostID).collection("Views").document(uniqueID).set(viewsMap);
                activity.startActivity(intent);
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
        private TextView mUsername, mTitle, mDesc, mLocation, mPrice, mDate;
        private ImageView mPostImage;
        private CircleImageView mProfileImage;
        private TextView mViewsNo;
        private ProgressBar mProgressBar;
        ItemClickListener itemClickListener;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPostImage = mView.findViewById(R.id.posted_house);
            mProgressBar = mView.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);

        }

        public void setUsernameText(String text){
            mUsername = mView.findViewById(R.id.user_name);
            mUsername.setText(text);
        }
        public void setDescriptionText(String text){
            mDesc = mView.findViewById(R.id.description);
            mDesc.setText(text);
        }
        public void setPostImage(String downloadUri){
            RequestOptions placeholderRequest = new RequestOptions().fitCenter();
            placeholderRequest.placeholder(R.drawable.placeholder);
            Glide.with(context).setDefaultRequestOptions(placeholderRequest).load(downloadUri).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                     mProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(mPostImage);
        }
        public void setProfileImage(String profileUri){
            mProfileImage = mView.findViewById(R.id.user_image);
            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.placeholder);
            Glide.with(context).setDefaultRequestOptions(placeholderRequest).load(profileUri).into(mProfileImage);
        }
        public void setTitleText(String text){
            mTitle = mView.findViewById(R.id.title);
            mTitle.setText(text);
        }
        public void setLocationText(String text){
            mLocation = mView.findViewById(R.id.location_text);
            mLocation.setText(text);
        }
        public void setPriceText(String text){
            mPrice = mView.findViewById(R.id.price);
            mPrice.setText(text);
        }
        public void setTimeText(String date){
            mDate = mView.findViewById(R.id.time_posted);
            mDate.setText(date);
        }
        public void updateViewsCount(int count){
            mViewsNo = mView.findViewById(R.id.views_no);
            if(count == 1){
             mViewsNo.setText(count + " View");
            } else {
                mViewsNo.setText(count + " Views");
            }
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
    public void updateList(List<AgentPost> newList){
        postList = new ArrayList<>();
        postList.addAll(newList);
        notifyDataSetChanged();
    }

}

