package me.cchiang.lookforthings.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import me.cchiang.lookforthings.R;
import me.cchiang.lookforthings.model.ChatsDetails;

public class ChatDetailsListAdapter extends BaseAdapter {
	
	private List<ChatsDetails> mMessages;
	private Context ctx;
	private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	
	public ChatDetailsListAdapter(Context context, List<ChatsDetails> messages) {
        super();
        this.ctx = context;
        this.mMessages = messages;
	}
	
	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mMessages.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatsDetails msg = (ChatsDetails) getItem(position);
        ViewHolder holder;
        if(convertView == null){
        	holder 				= new ViewHolder();
        	convertView			= LayoutInflater.from(ctx).inflate(R.layout.row_chat_details, parent, false);
        	holder.time 		= (TextView) convertView.findViewById(R.id.text_time);
        	holder.message 		= (TextView) convertView.findViewById(R.id.text_content);
			holder.lyt_thread 	= (CardView) convertView.findViewById(R.id.lyt_thread);
			holder.lyt_parent 	= (LinearLayout) convertView.findViewById(R.id.lyt_parent);
			holder.image_status	= (ImageView) convertView.findViewById(R.id.image_status);
        	convertView.setTag(holder);	
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.message.setText(msg.getContent());
		holder.time.setText(msg.getDate());

		if(msg.getSentFrom() != null){
			if(msg.getSentFrom().equals(user.getUid())){
				msg.setFromMe(true);
			}else{
				msg.setFromMe(false);
			}
		}

        if(msg.isFromMe()){
			holder.lyt_parent.setPadding(100, 10, 15, 10);
			holder.lyt_parent.setGravity(Gravity.RIGHT);
			holder.lyt_thread.setCardBackgroundColor(ctx.getResources().getColor(R.color.me_chat_bg));
        }else{
			holder.lyt_parent.setPadding(15, 10, 100, 10);
			holder.lyt_parent.setGravity(Gravity.LEFT);
			holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
			//holder.image_status.setImageResource(android.R.color.transparent);
        }
        return convertView;
	}

	/**
	 * remove data item from messageAdapter
	 * 
	 **/
	public void remove(int position){
		mMessages.remove(position);
	}
	
	/**
	 * add data item to messageAdapter
	 * 
	 **/
	public void add(ChatsDetails msg){
		mMessages.add(msg);
	}
	
	public static class ViewHolder{
//		public TextView messageTextView;
//        public TextView messengerTextView;
//        public CircleImageView messengerImageView;
//        public TextView timeTextView;
		public TextView time;
		public TextView message;
		public LinearLayout lyt_parent;
		public CardView lyt_thread;
		public ImageView image_status;
	}



}
