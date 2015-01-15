package com.pbylicki.cookbook.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment implements Comparable<Comment> {
    public Integer id;
    public Integer recipeId;
    public Integer ownerId;
    public String text;
    public String created;

    public Date getCreatedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try{
            return sdf.parse(created);
        }
        catch (Exception e){
            return new Date();
        }
    }

    @Override
    public int compareTo(Comment comment) {
        //from newest to oldest
        return -getCreatedDate().compareTo(comment.getCreatedDate());
    }
}

