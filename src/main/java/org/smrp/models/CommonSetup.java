package org.smrp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.mutiny.sqlclient.Row;
import java.time.LocalDateTime;


public class CommonSetup {
    
    private Long id;
    private String code;
    private String desc;
    private String ref;

    @JsonProperty("created_by")
    private Long createdBy;

    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    @JsonProperty("modified_by")
    private Long modifiedBy;

    @JsonProperty ("modified_date")
    private LocalDateTime modifiedDate;
    private Boolean deleted;

    @JsonProperty("deleted_by")
    private Long deletedBy;
    
    @JsonProperty("deleted_date")
    private LocalDateTime deletedDate;

    public static CommonSetup fromRow(Row row) {
        CommonSetup commonSetup = new CommonSetup();
        commonSetup.setId(row.getLong("id"));
        commonSetup.setCode(row.getString("code"));
        commonSetup.setDesc(row.getString("desc"));
        commonSetup.setRef(row.getString("ref"));
        commonSetup.setCreatedBy(row.getLong("created_by"));
        commonSetup.setCreatedDate(row.getLocalDateTime("created_date"));
        commonSetup.setModifiedBy(row.getLong("modified_by"));
        commonSetup.setModifiedDate(row.getLocalDateTime("modified_date"));
        commonSetup.setDeleted(row.getBoolean("deleted"));
        commonSetup.setDeletedBy(row.getLong("deleted_by"));
        commonSetup.setDeletedDate(row.getLocalDateTime("deleted_date"));
        return commonSetup;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }
}
