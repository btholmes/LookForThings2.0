package me.cchiang.lookforthings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.cchiang.lookforthings.Game;
import me.cchiang.lookforthings.R;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.GameViewHolder> {


    private SparseBooleanArray selectedItems;

    private List<Game> listItems, filterList;
    private Context mContext;


    // for item click listener
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Game obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // for item long click listener
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemClick(View view, Game obj, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }


    public class GameViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView time;
        public ImageView image;
        public LinearLayout lyt_parent;

        public GameViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            time = (TextView) v.findViewById(R.id.time);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);

        }
    }


        public MainListAdapter(Context context, List<Game> listItems) {
            this.listItems = listItems;
            this.mContext = context;
            this.filterList = new ArrayList<>();
            this.filterList = this.listItems;
            selectedItems = new SparseBooleanArray();

        }

        @Override
        public MainListAdapter.GameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_chats, viewGroup,  false);
            MainListAdapter.GameViewHolder viewHolder = new MainListAdapter.GameViewHolder(view);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(MainListAdapter.GameViewHolder holder, final int position) {

            final Game listItem = filterList.get(position);
            holder.title.setText(listItem.getDate());
            holder.content.setText("Chair Table Person");

            holder.title.setText(listItem.getDate());

            holder.time.setText(listItem.getDate());
            holder.content.setText("Chair, Person, Laptop");
//            Picasso.with(ctx).load(c.getFriend().getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);

            // Here you apply the animation when the view is bound
            setAnimation(holder.itemView, position);
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, listItem, position);
                    }
                }
            });

            holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemClick(view, listItem, position);
                    }
                    return false;
                }
            });

            holder.lyt_parent.setActivated(selectedItems.get(position, false));



        }

        @Override
        public int getItemCount() {
            return (null != filterList ? filterList.size() : 0);
        }

    /**
     * Here is the key method to apply the animation
     */
    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}