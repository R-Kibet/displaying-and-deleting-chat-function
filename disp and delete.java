package com.example.clone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clone.Models.MessageModel;
import com.example.clone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter{

    //Create 2 variables

    ArrayList<MessageModel> messageModels; //stores message
    Context context;
    String recId;


    //uniquely identify sender and receiver
    //create 2 more variables after adding overRide  method line 53
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE)
        {
        View view = LayoutInflater.from(context).inflate(R.layout.samp_sender,parent,false);
        return  new SenderViewHolder(view);

        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.samp_receiver,parent,false);
            return  new ReceiverViewHolder(view);


        }
    }

    //add this override method
    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))

        {
            return  SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);

        /*
        Added line giving feature of delete
        giving an option for sender to delete a txt without deleting at receiver end
         */

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are U sure ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //input the delete function if clicked yes
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);


                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                dialogInterface.dismiss();
                            }
                        })

                        .show();//this is required so as for the dialog to be displayed

                return false;
            }
        }); // Delete message function


        if(holder.getClass() == SenderViewHolder.class)


        {
            ((SenderViewHolder) holder).senderMess.setText(messageModel.getMessage());

            //creating a the actual time stamp
            Date date = new Date(messageModel.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");//am pm format
            String strDate = simpleDateFormat.format(date);//stored variable sa a string format
            ((SenderViewHolder) holder).senderTIme.setText(strDate);


        }

        else{

            ((ReceiverViewHolder) holder).receiverMess.setText(messageModel.getMessage());

            Date date = new Date(messageModel.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");//am pm format
            String strDate = simpleDateFormat.format(date);
            ((ReceiverViewHolder) holder).receiverTIme.setText(strDate);


        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    //need to create 2 view holder class for the sender and receiver

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMess, receiverTIme;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);


            receiverMess = itemView.findViewById(R.id.receivertxt);
            receiverTIme = itemView.findViewById(R.id.rectime);
        }
    }


    public class  SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMess , senderTIme;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMess = itemView.findViewById(R.id.sendertxt);
            senderTIme = itemView.findViewById(R.id.sendtime);

        }
    }
}
