/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.finacialStock.model;

import java.math.BigDecimal;

/**
 *
 * @author k.skowronski
 */
public class DocItemDTO {
    
    private String rozNumer;
    
    private String rozTyp;
    
    private BigDecimal wn;
    private BigDecimal ma;
    private BigDecimal saldo;
    
    private String rozliczony;
    private String rozliczonyNaDzis;
    
    private String okres;
    private BigDecimal rozId;

    public String getRozNumer() {
        return rozNumer;
    }

    public void setRozNumer(String rozNumer) {
        this.rozNumer = rozNumer;
    }

    public String getRozTyp() {
        return rozTyp;
    }

    public void setRozTyp(String rozTyp) {
        this.rozTyp = rozTyp;
    }

    public BigDecimal getWn() {
        return wn;
    }

    public void setWn(BigDecimal wn) {
        this.wn = wn;
    }

    public BigDecimal getMa() {
        return ma;
    }

    public void setMa(BigDecimal ma) {
        this.ma = ma;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getRozliczony() {
        return rozliczony;
    }

    public void setRozliczony(String rozliczony) {
        this.rozliczony = rozliczony;
    }

    public String getRozliczonyNaDzis() {
        return rozliczonyNaDzis;
    }

    public void setRozliczonyNaDzis(String rozliczonyNaDzis) {
        this.rozliczonyNaDzis = rozliczonyNaDzis;
    }

    public String getOkres() {
        return okres;
    }

    public void setOkres(String okres) {
        this.okres = okres;
    }

    public BigDecimal getRozId() {
        return rozId;
    }

    public void setRozId(BigDecimal rozId) {
        this.rozId = rozId;
    }

    

}
