/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pl.common.dao.GenericDao;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.OrganisationApi;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProductsSuppliers;
import pl.vo.utils.VOUtils;

/**
 *
 * @author Piotr
 */
@Stateless(name = "UnitsProductsSuppliersApi",mappedName = "UnitsProductsSuppliersApi")
@LocalBean
public class UnitsProductsSuppliersApi extends GenericDao<UnitsProductsSuppliers, Long>  implements Serializable 
{
    
    @EJB
    ProductsApi productsApi; 
    
    @EJB
    OrganisationApi organisationApi; 
  
    @EJB
    CompanysApi companyApi;
    
     public UnitsProductsSuppliersApi(){
            super(UnitsProductsSuppliers.class) ;
     }
   
     public List<UnitsProductsSuppliers> findAll()
     { 
       List<UnitsProductsSuppliers> ret = super.findAll();
       return ret; 
     }
     
     public List<UnitsProductsSuppliers> findForUnit( Long unitId, Date atDate )
     {
          CriteriaBuilder cb =  em.getCriteriaBuilder(); 
          CriteriaQuery<UnitsProductsSuppliers> cq = cb.createQuery(UnitsProductsSuppliers.class);
        Root<UnitsProductsSuppliers> units = cq.from( UnitsProductsSuppliers.class);
        cq.select(units);
        
         Predicate predUnitId =  cb.equal( units.get("unit").get("id"), cb.literal( unitId )); 
        if ( atDate != null   )
        {
            // add date condition lessThanOrEqualTo
            //cb.or(  cb.isNull(units.get("dateFrom")), cb.lessThanOrEqualTo( units.<Date>get("dateFrom"), cb.literal(atDate) ) ) , 
            //cb.or(  cb.isNull(units.get("dateTill")), cb.greaterThanOrEqualTo( units.<Date>get("dateTill"), cb.literal(atDate)))
            Predicate predDate = cb.and
            ( 
                cb.or(  cb.isNull(units.get("dateFrom")), cb.lessThanOrEqualTo( units.<Date>get("dateFrom"), atDate ) ) , 
                cb.or(  cb.isNull(units.get("dateTill")), cb.greaterThanOrEqualTo( units.<Date>get("dateTill"), atDate ))
            );
            
            cq.where ( cb.and( predUnitId, predDate ));
        }
        else
            cq.where( predUnitId );
        List<UnitsProductsSuppliers> ret = ( List<UnitsProductsSuppliers> ) em.createQuery( cq).getResultList(); 
        return ret; 
     }
    
     public UnitsProductsSuppliers save( UnitsProductsSuppliers item) throws VOWrongDataException
     {
        return super.save( item );
       
     }
     
     public String delete( UnitsProductsSuppliers item )throws VOWrongDataException
     {
         return super.delete( item );
     }
     
     
     public UnitsProductsSuppliers addProductToUnit( Long prodId, Long unitId, Long supplierId, Date dateFrom, Date dateTill) throws VOWrongDataException
     {
         UnitsProductsSuppliers up = new UnitsProductsSuppliers();
         Product prod = productsApi.get( prodId ) ; 
         OrganisationUnit unit = organisationApi.get(unitId);
         Company company = companyApi.get( supplierId );
         
         
         up.setProduct(prod);
         up.setUnit( unit );
         up.setSupplier( company );
         up.setDateFrom( dateFrom );
         up.setDateTill( dateTill );
         
         up = save( up );
         return up; 
     }
     
     
     
     // przypisuje dostawcow w przekazanym dokumencie
     public void assignSuppliers( Document doc ) throws VOWrongDataException
     {
         if (doc.getDateDelivery() == null)
             throw new VOWrongDataException("Błąd UPSA-119 przekazany dokument nie ma daty dostawy");
         
         if (doc.getCompanyUnit() == null)
             throw new VOWrongDataException("Błąd UPSA-122 przekazany dokument nie ma przypisanego obiektu");
         // read all for unit
         List<UnitsProductsSuppliers> suppliers = findForUnit( doc.getCompanyUnit().getId(), doc.getDateDelivery() );
         
         for( DocumentItem item:doc.getItems() )
         {
             UnitsProductsSuppliers prodSup = null; 
             for ( UnitsProductsSuppliers sup : suppliers ){
                 if ( sup.getProduct().equals( item.getProduct() ))
                 {
                     prodSup = sup; 
                     break; 
                 }
             }
             
             if ( prodSup == null )
                 throw new VOWrongDataException("Błąd UPSA-132 - nie znaleziono dostawcy dla produktu:" + item.getProduct().getAbbr() + " na dzień:"+
                         VOUtils.formaDateYYYYMMDD( doc.getDateDelivery() )+ " dla obiektu "+ doc.getCompanyUnit().getName());
             
             
             item.setUnitProductSupplier(prodSup);
         }
     }
     
    
}