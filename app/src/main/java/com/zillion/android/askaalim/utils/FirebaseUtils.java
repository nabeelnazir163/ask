package com.zillion.android.askaalim.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseUtils {
    //I'm creating this class for similar reasons as the Constants class, and to make my code a bit
    //cleaner and more well managed.

    public static DatabaseReference getUserRef(String email){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.USERS_KEY)
                .child(email);
    }

    public static DatabaseReference getPostRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POST_KEY);
    }

    public static DatabaseReference getBookmarksRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.BOOKMARKS_KEY);
    }

    public static DatabaseReference getPostLikedRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POST_LIKED_KEY);
    }

    public static DatabaseReference getPostLikedRef(String postId){
        return getPostLikedRef().child(getCurrentUser().getEmail()
                .replace(".",","))
                .child(postId);
    }

    public static DatabaseReference getAnswerLikedRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.ANSWER_LIKED_KEY);
    }

    public static DatabaseReference getNotificationRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.NOTIFICATION);
    }

    public static DatabaseReference getMessageRef(){
        return FirebaseDatabase.getInstance()
                .getReference("message");
    }

    public static DatabaseReference getChatRef(){
        return FirebaseDatabase.getInstance()
                .getReference("chat");
    }

    public static DatabaseReference getRootRef(){
        return FirebaseDatabase.getInstance()
                .getReference();
    }

    public static DatabaseReference postViewRef(){
        return FirebaseDatabase.getInstance()
                .getReference().child("postview");
    }

    public static DatabaseReference getAnswerLikedRef(String answerId, String postId){
        return getAnswerLikedRef().child(getCurrentUser().getEmail()
                .replace(".",","))
                .child(postId)
                .child(answerId);
    }

    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUid(){
        String path = FirebaseDatabase.getInstance().getReference().push().toString();
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static StorageReference getImageSRef(){
        return FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES);
    }

    public static StorageReference getAnswerImageSRef(){
        return FirebaseStorage.getInstance().getReference(Constants.ANSWER_IMAGES);
    }

    public static StorageReference getAudioRef(){
        return FirebaseStorage.getInstance().getReference(Constants.AUDIO_FILE);
    }

    public static DatabaseReference getMyPostRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.MY_POSTS)
                .child(getCurrentUser().getEmail().replace(".",","));
    }

    public static DatabaseReference getmypostReference(){

        return FirebaseDatabase.getInstance().getReference(Constants.MY_POSTS);

    }

    public static DatabaseReference getMyAnsRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.MY_ANSWERS)
                .child(getCurrentUser().getEmail().replace(".",","));
    }

    public static DatabaseReference getmyanswerReference(){

        return FirebaseDatabase.getInstance().getReference(Constants.MY_ANSWERS );

    }

    public static DatabaseReference getCommentRef(String postId){
        return FirebaseDatabase.getInstance().getReference(Constants.COMMENTS_KEY)
                .child(postId);
    }

    public static DatabaseReference getFollowers(){
        return FirebaseDatabase.getInstance().getReference(Constants.FOLLOWERS_KEY);
    }



    public static DatabaseReference getAnswerRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.ANSWERS_KEY);

    }

    public static DatabaseReference getC_ReplyRef(String commentId){
        return FirebaseDatabase.getInstance().getReference(Constants.COMMENT_REPLY)
                .child(commentId);
    }


    public static DatabaseReference getMyRecordRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.USER_RECORD)
                .child(getCurrentUser().getEmail().replace(".",","));
    }

    public static void addToMyRecord(String node, final String id){
        getMyRecordRef().child(node).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ArrayList<String> myRecordCollection;
                if(mutableData.getValue() == null){
                    myRecordCollection = new ArrayList<String>(1);
                    myRecordCollection.add(id);
                }else{
                    myRecordCollection = (ArrayList<String>) mutableData.getValue();
                    myRecordCollection.add(id);
                }

                mutableData.setValue(myRecordCollection);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

}