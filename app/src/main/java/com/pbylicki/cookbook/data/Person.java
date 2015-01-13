package com.pbylicki.cookbook.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person implements Serializable {
    public Integer id;
    public String name;
    public String company;
    public String phoneNumber;
    public Integer ownerId;

}
