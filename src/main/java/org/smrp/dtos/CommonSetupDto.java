package org.smrp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CommonSetupDto {
    
    @NotBlank(message = "Code is required")
    @Size(max = 30, message = "Code must not exceed 30 characters")
    private String code;

    @NotBlank(message = "Desc is required")
    @Size(max = 300, message = "Desc must not exceed 300 characters")
    private String desc;

    @NotBlank(message = "Ref is required")
    @Size(max = 200, message = "Ref must not exceed 200 characters")
    private String ref;
    
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
}
