package com.pbylicki.cookbook.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe implements Serializable, Comparable<Recipe> {
    public Integer id;
    public Integer ownerId;
    public String title;
    public String introduction;
    public String ingredients;
    public String steps;
    public String created;
    public Integer preparationMinutes;
    public Integer cookingMinutes;
    public Integer servings;
    @JsonIgnore
    public String pictureBytes;
    @JsonIgnore
    public String author;


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
    public int compareTo(Recipe recipe) {
        //from newest to oldest
        return -getCreatedDate().compareTo(recipe.getCreatedDate());
    }

    public void decodeAndSetImage(ImageView image) {
        // zamień ciąg tekstowy Base-64 na tablicę bajtów
        byte[] decodedString = Base64.decode(pictureBytes, Base64.DEFAULT);
        // utwórz bitmapę na podstawie ciągu bajtów z obrazem JPEG
        Bitmap decodedBytes = BitmapFactory.decodeByteArray(decodedString, 0,
                decodedString.length);
        // wstaw bitmapę do komponentu ImageView awatara
        image.setImageBitmap(decodedBytes);
    }
}
