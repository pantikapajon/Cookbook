package com.pbylicki.cookbook.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {
    public Integer id;
    public String display_name;
    public String created_date;
    public String last_modified_date;
    public String email;

    //returns Date object from from created string attribute
    public Date getDate(String attr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try{
            return sdf.parse(attr);
        }
        catch (Exception e){
            return new Date();
        }
    }

    public String getShortDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd MMM yyyy");
        return sdf.format(date);
    }
}
