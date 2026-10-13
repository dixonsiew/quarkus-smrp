package org.smrp.models;

import io.vertx.mutiny.sqlclient.Row;


public class Role {

    private Long id;
    private String name;

    public static Role fromRow(Row row) {
        Role role = new Role();
        role.setId(row.getLong("id"));
        role.setName(row.getString("name"));
        return role;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
