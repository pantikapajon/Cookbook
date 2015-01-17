package com.pbylicki.cookbook.data;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LikeList {
    @JsonProperty("record")
    public List<Like> records = new ArrayList<Like>();

    public HashSet<Integer> getLikesForRecipe(){
        HashSet<Integer> result = new HashSet<Integer>();
        for(Like like : records) result.add(like.ownerId);
        return result;
    }
}
