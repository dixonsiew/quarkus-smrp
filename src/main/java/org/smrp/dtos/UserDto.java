package org.smrp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public class UserDto {
    
    @NotBlank(message = "Username is required")
    @Size(max = 150, message = "Username must not exceed 150 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(max = 150, message = "Password must not exceed 150 characters")
    private String password;

    @JsonProperty("first_name")
    @NotBlank(message = "Firstname is required")
    @Size(max = 150, message = "Firstname must not exceed 150 characters")
    private String firstname;

    @JsonProperty("last_name")
    @Size(max = 150, message = "Lastname must not exceed 150 characters")
    private String lastname;

    @JsonProperty("role_id")
    @Positive 
    private Long roleId;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public Long getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
