package com.pbylicki.cookbook.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {
    public Integer id;
    public String display_name;
    public String created_date;
    public String last_modified_date;
    public String email;
}
