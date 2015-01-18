package com.pbylicki.cookbook.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Picture {
    public Integer id;
    public Integer ownerId;
    public String base64bytes;
    public Integer recipeId;
}
