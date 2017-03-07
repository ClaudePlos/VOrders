/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.api;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import pl.common.dao.VoBeanBase;
import pl.vo.VOConsts;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.products.model.Product;
import pl.vo.security.model.User;
import pl.vo.utils.VOUtils;

/**
 *
 * @author Piotr
 */
@Stateless(name = "PriceListsApi", mappedName = "PriceListsApi")
@LocalBean
public class PriceListsApi extends VoBeanBase implements Serializable {

    @PersistenceContext(name = "pu")
    protected EntityManager em;

    public void assignPricesInDocument(Document doc) throws VOWrongDataException {
        if (doc.getDateDelivery() == null) {
            throw new VOWrongDataException("Błąd PLA-119 przekazany dokument nie ma daty dostawy");
        }

        if (doc.getCompanyUnit() == null) {
            throw new VOWrongDataException("Błąd PLA-122 przekazany dokument nie ma przypisanego obiektu");
        }

        for (DocumentItem item : doc.getItems()) 
        {
            if ( item.getUnitProductSupplier() == null ) {
                throw new VOWrongDataException("Błąd PLA-47 - nie można sprawdzić ceny gdyż towar " + item.getProduct().getName() + " nie ma przypisanego dostawcy ");
            }
            DocumentItem price = findPriceForProduct(item.getProduct(), item.getUnitProductSupplier().getSupplier(), doc.getDateDelivery());
            item.setPriceItem( price );
            item.setUnitPriceNet( price.getUnitPriceNet() );
        }
    }
    
    

    public DocumentItem findPriceForProduct(Product prod,Company supplier,  Date date) throws VoNoResultException {
        
        User user = this.getLoggedUser();
        
        String sql = "select di from DocumentItem di "
                + " where di.product.id = :prodId "
                + " and di.document.type = :docTypePriceList"
                + " and di.document.status =:statusAccepted"
                + " and di.document.supplier.id = :supplierId "
                + " and ( di.document.validFrom is null or di.document.validFrom <= :date ) "
                + " and ( di.document.validTill is null or di.document.validTill >= :date ) ";
//                + " and di.instanceCode = :instanceCode ";

        try {

            DocumentItem ret = (DocumentItem) em.createQuery(sql)
                    .setParameter("prodId", prod.getId())
                    .setParameter("supplierId", supplier.getId())
                    .setParameter("docTypePriceList", VOConsts.DOC_TYPE_PRICE_LIST)
                    .setParameter("statusAccepted", VOConsts.DOC_STATUS_ACCEPTED)
//                    .setParameter("instanceCode",user.getInstanceCode())
                    .setParameter("date",date).getSingleResult();
            
            return ret; 
        } catch (NonUniqueResultException nre) {
            throw new VoNoResultException("Znaleziono kilka pozycji cennika dla towaru :" + prod.getName() + " na dzień " + VOUtils.formaDateYYYYMMDD(date) + " u dostawcy " + 
                  supplier.getAbbr() + " instancja: " + user.getInstanceCode()  );
        }
         catch (NoResultException nore) {
            throw new VoNoResultException("Nie znaleziono cennika dla towaru :" + prod.getName() + " na dzień " + VOUtils.formaDateYYYYMMDD(date) + " u dostawcy " + 
                  supplier.getAbbr()  + " instancja: " + user.getInstanceCode()  );
        }
    }
    
    
    // ks add 2017-03-07 -> szukanie cen na cenniku dostawcy, zrobiłem dla zamienikow
    public void assignPricesInDocumentInSupplier(Document doc) throws VOWrongDataException {
        if (doc.getDateDelivery() == null) {
            throw new VOWrongDataException("Błąd PLA-119 przekazany dokument nie ma daty dostawy");
        }

        if (doc.getCompanyUnit() == null) {
            throw new VOWrongDataException("Błąd PLA-122 przekazany dokument nie ma przypisanego obiektu");
        }

        for (DocumentItem item : doc.getItems()) 
        {
           
            DocumentItem price = findPriceForProductInSupplier(item.getProduct(), doc.getDateDelivery());
            item.setPriceItem( price );
            item.setUnitPriceNet( price.getUnitPriceNet() );
        }
    }
    
    
    public DocumentItem findPriceForProductInSupplier(Product prod,   Date date) throws VoNoResultException {
        
        User user = this.getLoggedUser();
        
        String sql = "select di from DocumentItem di "
                + " where di.product.id = :prodId "
                + " and di.document.type = :docTypePriceList"
                + " and di.document.status =:statusAccepted"
                + " and ( di.document.validFrom is null or di.document.validFrom <= :date ) "
                + " and ( di.document.validTill is null or di.document.validTill >= :date ) ";
//                + " and di.instanceCode = :instanceCode ";

        try {

            DocumentItem ret = (DocumentItem) em.createQuery(sql)
                    .setParameter("prodId", prod.getId())
                    .setParameter("docTypePriceList", VOConsts.DOC_TYPE_PRICE_LIST)
                    .setParameter("statusAccepted", VOConsts.DOC_STATUS_OPEN)
//                    .setParameter("instanceCode",user.getInstanceCode())
                    .setParameter("date",date).getSingleResult();
            
            return ret; 
        } catch (NonUniqueResultException nre) {
            throw new VoNoResultException("Znaleziono kilka pozycji cennika dla towaru :" + prod.getName() + " na dzień " + VOUtils.formaDateYYYYMMDD(date) + " u dostawcy " + 
                   " instancja: " + user.getInstanceCode()  );
        }
         catch (NoResultException nore) {
            throw new VoNoResultException("Nie znaleziono cennika dla towaru :" + prod.getName() + " na dzień " + VOUtils.formaDateYYYYMMDD(date) + " prodId: " + 
               prod.getId() +  " instancja: " + user.getInstanceCode()  );
        }
    }
    
    
}
