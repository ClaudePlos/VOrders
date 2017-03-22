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
public class DocDTO {
    
    private String numerWlasny;
    private String numerObcy;
    
    private String dataWystawienia;
    private String dataZaksiegowania;
    private String dataWymagalnosci;
    
    private String zaplata;
    private String rozliczona;
    
    private String dokOpis;
    private String plOpis;
    
    private BigDecimal wn;
    private BigDecimal ma;

    public String getNumerWlasny() {
        return numerWlasny;
    }

    public void setNumerWlasny(String numerWlasny) {
        this.numerWlasny = numerWlasny;
    }

    public String getNumerObcy() {
        return numerObcy;
    }

    public void setNumerObcy(String numerObcy) {
        this.numerObcy = numerObcy;
    }

    public String getDataWystawienia() {
        return dataWystawienia;
    }

    public void setDataWystawienia(String dataWystawienia) {
        this.dataWystawienia = dataWystawienia;
    }

    public String getDataZaksiegowania() {
        return dataZaksiegowania;
    }

    public void setDataZaksiegowania(String dataZaksiegowania) {
        this.dataZaksiegowania = dataZaksiegowania;
    }

    public String getDataWymagalnosci() {
        return dataWymagalnosci;
    }

    public void setDataWymagalnosci(String dataWymagalnosci) {
        this.dataWymagalnosci = dataWymagalnosci;
    }

    public String getZaplata() {
        return zaplata;
    }

    public void setZaplata(String zaplata) {
        this.zaplata = zaplata;
    }

    public String getRozliczona() {
        return rozliczona;
    }

    public void setRozliczona(String rozliczona) {
        this.rozliczona = rozliczona;
    }

    public String getDokOpis() {
        return dokOpis;
    }

    public void setDokOpis(String dokOpis) {
        this.dokOpis = dokOpis;
    }

    public String getPlOpis() {
        return plOpis;
    }

    public void setPlOpis(String plOpis) {
        this.plOpis = plOpis;
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

    
    
}
