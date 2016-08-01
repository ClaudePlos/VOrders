/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.numeration.model.api;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringUtils;
import pl.vo.VOConsts;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.numeration.model.Numeration;
import pl.vo.utils.VOUtils;

/**
 *
 * @author Piotr
 */
@Stateless(name = "NumerationApi", mappedName = "NumerationApi")
@LocalBean
public class NumerationApi implements Serializable
{
    
      @PersistenceContext(name = "pu")
    protected EntityManager em; 
      
      
      
    public void fillNumerIfNotExists( Document doc )throws VOWrongDataException
    {
        if ( doc.getOwnNumber() != null )
            return;
        else 
            doc.setOwnNumber( getNumberForDocument( doc ) );
    }
    public String getNumberForDocument( Document doc )throws VOWrongDataException
    {
        SimpleDateFormat df = new SimpleDateFormat("YY/MM");
        
        String ret = "";
        Long newNumber = getNewNumberForType( doc.getType() , doc.getDateOperation() );
        String num = newNumber.toString(); 
        num = StringUtils.leftPad(num, 4, "0");
        
       
        
        ret = doc.getType()+"/";
        
        ret = StringUtils.replace(ret, "VO_", "");
        
        if ( doc.getType().equals( VOConsts.DOC_TYPE_ZWK )){
            ret = doc.getCompanyUnit().getCode()+"/";
        }
        
        if ( doc.getDateOperation() == null)
            throw new VOWrongDataException("Błąd NA-64 - nie można wygenerować numeru gdyż dokument nie ma daty operacji");
        ret += df.format( doc.getDateOperation() );
        ret += "/" + num; 
        
        return ret; 
    }
    
    
    public Long getNewNumberForType(String type, Date date) throws VOWrongDataException
    {
        // try to find
        String sql = "select n from Numeration n where n.docType = :docType "
                + "and n.seriesDate = :date " ; 
        
        try { 
            Numeration num = (Numeration)  em.createQuery(sql).setParameter("docType", type).setParameter("date", date).getSingleResult();
            num.setLastNumber( num.getLastNumber() + 1);
            em.persist( num );
            return num.getLastNumber(); 
        }
        catch ( NonUniqueResultException nure) {
            throw new VOWrongDataException("Błąd NA47 - znaleziono kilka serii numeracji dla typu:"+type+" w okresie:"+VOUtils.formaDateYYYYMMDD(date));
        }
        catch( NoResultException nre )
        {
            // create new 
            Numeration num = new Numeration();
            num.setDocType( type );
            num.setLastNumber( new Long(1));
            num.setSeriesDate( date );
           num = em.merge(num);
           em.persist( num );
           return num.getLastNumber();
        }
    }
}
