package org.smrp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.mutiny.sqlclient.Row;
import java.time.LocalDateTime;
import java.util.List;


public class User {

    private Long id;
    private String username;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
    
    @JsonProperty("roles")
    private List<Role> roles;

    public static User fromRow(Row row) {
        User user = new User();
        user.setId(row.getLong("id"));
        user.setUsername(row.getString("username"));
        user.setFirstName(row.getString("first_name"));
        user.setLastName(row.getString("last_name"));
        user.setPassword(row.getString("password"));
        user.setLastLogin(row.getLocalDateTime("last_login"));
        return user;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
