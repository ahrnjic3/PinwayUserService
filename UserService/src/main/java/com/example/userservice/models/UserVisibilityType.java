package com.example.userservice.models;

import com.example.userservice.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
public class UserVisibilityType {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 20, message = "Type must contain less than 20 characters")
    @Pattern(regexp = "^[A-Z]*$", message = "Type must contain only uppercase letters")
    private String type;

    @JsonIgnoreProperties("userVisibilityType")
    @OneToMany(mappedBy = "userVisibilityType")

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}