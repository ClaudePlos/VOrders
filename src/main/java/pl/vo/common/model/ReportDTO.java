/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.common.model;

/**
 *
 * @author k.skowronski
 */
public class ReportDTO {
    
    
    private int id;
    
    private String name;

    public ReportDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
