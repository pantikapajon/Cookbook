package com.pbylicki.cookbook.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe implements Serializable {
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
}
