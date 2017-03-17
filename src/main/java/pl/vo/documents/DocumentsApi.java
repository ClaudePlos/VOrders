/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.ConstraintViolationException;
import pl.common.dao.GenericDao;
import pl.vo.VOConsts;
import pl.vo.common.VoUserSession;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.security.model.User;
import pl.vo.security.model.UsersCompanyUnits;
import pl.vo.utils.VOUtils;

/**
 *
 * @author Piotr
 */
//@SessionScoped
@Stateful(name = "DocumentsApi", mappedName = "DocumentsApi")
@LocalBean
public class DocumentsApi extends GenericDao<Document, Long> implements Serializable {

    public DocumentsApi() {
        super(Document.class);
    }

    public List<Document> findAll()
    {
        List<Predicate> predicates = new ArrayList<Predicate>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Document> cq = cb.createQuery(classType);
        Root<Document> root = cq.from(classType);
        cq.select(root);

        addPermistionPredicates(predicates, cb, cq, root);
       addInstanceCodeCriterias(cb, cq, root,predicates);
        List<Document> ret = (List<Document>) em.createQuery(cq).getResultList();
        
        
        return ret;
    }
    
    

    public List<Document> findDocuments(String[] types, Long orgUnitId, Long supplId, Date month, String username) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Document> cq = cb.createQuery(classType);
        Root<Document> root = cq.from(classType);
        cq.select(root);
        
        // type 
        predicates.add(root.get("type").in(types));
        if (orgUnitId != null) {
            predicates.add(cb.equal(root.get("companyUnit").get("id"), cb.literal(orgUnitId)));
        }
        if (supplId != null) {
            predicates.add(cb.equal(root.get("supplier").get("id"), cb.literal(supplId)));
        }

        if (month != null) {
            predicates.add(cb.between(root.<Date>get("dateOperation"), cb.literal(VOUtils.firstDayOfMonth(month)), cb.literal(VOUtils.lastDayOfMonth(month))));
        }
        
        cq.orderBy( cb.desc(root.get("dateOperation")) ); // ks add sort 

        // apply permistions 
        addPermistionPredicates( predicates, cb, cq, root);
        addInstanceCodeCriterias(cb, cq, root,predicates);
        //
      //  cq.where(cb.and(predicates.toArray(new Predicate[1])));

        List<Document> ret = (List<Document>) em.createQuery(cq).getResultList();
        return ret;
    }

    public List<Document> listChildDocuments(Long parentId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Document> cq = cb.createQuery(classType);
        Root<Document> root = cq.from(classType);
        cq.select(root);
        cq.where(cb.equal(root.get("sourceDocument").<Long>get("id"), cb.literal(parentId)));
        
        List<Document> ret = (List<Document>) em.createQuery(cq).getResultList();
        return ret;
    }

    public Document save(Document doc) throws VOWrongDataException
    {
       User loggedUser = getLoggedUser();
        for (DocumentItem di : doc.getItems()) {
            if (di.getId() != null && di.getId().equals(new Long(0))) {
                di.setId(null);
            }
            di.setDocument(doc);
            VoUserSession.fillAudit(di, loggedUser);
        }

        try {
            if (doc.getId() != null) {
                doc = em.merge(doc);
            }

            VoUserSession.fillAudit(doc, loggedUser);
            em.persist(doc);
            em.flush();
        } catch (ConstraintViolationException cve) {
            throw VOWrongDataException.getForConstraintViolation("Nie udało się zapisać dokumentu", cve);
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się zapisać dokumentu:" + e.getMessage());
        }
        return doc;
    }

    public Document changeStatus(Long docId, String newStatus) throws VOWrongDataException {
        Document doc = get(docId);

        doc = changeStatus(doc, newStatus);
        return doc;
    }

    public Document changeStatus(Document doc, String newStatus, Boolean changeStatusForItems) throws VOWrongDataException {

        for (DocumentItem item : doc.getItems()) {
            item.setStatus(newStatus);
        }
        doc.setStatus(newStatus);
        doc = save(doc);
        return doc;

    }

    public Document changeStatus(Document doc, String newStatus) throws VOWrongDataException {

        return changeStatus(doc, newStatus, true);

    }

    public void recalculateDocument(Document doc) throws VOWrongDataException {

        BigDecimal sumNet = new BigDecimal(0);
        BigDecimal sumTax = new BigDecimal(0);
        BigDecimal sumBrut = new BigDecimal(0);

        for (DocumentItem item : doc.getItems()) {

            if (item.getAmount() == null) {
                throw new VOWrongDataException("Błąd w dokumencie - pozycja dla towaru:" + item.getProduct().getAbbr() + " nie ma ilości");
            }

            if (item.getUnitPriceNet() == null) {
                throw new VOWrongDataException("Błąd w dokumencie -towar:" + item.getProduct().getAbbr() + " nie ma cemy");
            }

            BigDecimal net = item.getUnitPriceNet().multiply(new BigDecimal(item.getAmount().intValue()));
            item.setValueNet(net);
            // 
            BigDecimal tax = net.multiply(item.getProduct().getTaxRate()).setScale(2);
            tax = tax.divide(new BigDecimal(100),RoundingMode.HALF_UP).setScale(2);
            item.setValueTax(tax);
            item.setValueBrut(net.add(tax));

            sumNet = sumNet.add(net);
            sumTax = sumTax.add(tax);
            sumBrut = sumBrut.add(item.getValueBrut());
        }

        doc.setValueNet(sumNet);
        doc.setValueBrut(sumBrut);
        doc.setValueTax(sumTax);

    }

    // adds user permistion predicate
    public void addPermistionPredicates( List<Predicate> predicates, CriteriaBuilder cb,CriteriaQuery cq,  Path<Document> path) {

        // and orgUni == null or exists ( select 1 from usersORgUnits where unit = unit and user = user ) 
        Predicate prNull = cb.isNull(path.get("companyUnit"));
        
        // subquery
        Subquery<UsersCompanyUnits> subquery = cq.subquery(UsersCompanyUnits.class);
        Root table2 = subquery.from(UsersCompanyUnits.class);
        subquery.select(table2);
        subquery.where(
                cb.and ( 
                        cb.equal( table2.get("companyUnit"), path.get("companyUnit")), 
                        cb.equal( table2.get("user"),cb.literal( getLoggedUser() ))
                )
        );

        Predicate prExists = cb.exists( subquery );
        
        predicates.add( cb.or( prNull, prExists));
         
    }
    
    public Document createDocumetCopy( Document source) throws VOWrongDataException
    {
        
        Document ndoc = new Document();
        ndoc.setType( source.getType() );
        ndoc.setStatus( VOConsts.DOC_STATUS_OPEN );
        ndoc.setSupplier( source.getSupplier() );
        ndoc.setClient( source.getClient() );
        ndoc.setCompanyUnit( source.getCompanyUnit() );
        ndoc.setDateOperation( source.getDateOperation() );
        for( DocumentItem item : source.getItems()){
            DocumentItem nitem = new DocumentItem();
            nitem.setProduct( item.getProduct());
            nitem.setUnitPriceNet( item.getUnitPriceNet() );
            nitem.setAmount( item.getAmount());
            
            ndoc.getItems().add( nitem );
        }
        
        ndoc   = this.save( ndoc );
        return ndoc; 
    }
    
    
    public void checkIdDocAndIdItems(Document doc) throws VOWrongDataException
    {
        Document docInOrder = getDocumentForOwnNumberAndData( doc.getExternalNumber(), doc.getDateDelivery() );
        doc.setId(  docInOrder.getId() );
        
        for ( DocumentItem itemOrder : docInOrder.getItems() )
        {
            for ( DocumentItem item : doc.getItems() )
            {
                if ( item.getProduct().getExternalCode().equals( itemOrder.getProduct().getExternalCode() ))
                {
                    item.setId( itemOrder.getId() );
                }
            }
        }
        
    }
    
    
    public Document getDocumentForOwnNumberAndData(String externalNumber, Date dateDelivery) throws VoNoResultException {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Document> cq = cb.createQuery(classType);
        Root<Document> root = cq.from(classType);
        
        Predicate eq = cb.and( cb.equal(root.get("ownNumber"), cb.literal(externalNumber)),
                               cb.equal(root.get("dateDelivery"), cb.literal(dateDelivery))
                                );
        cq.where(eq);
        cq.select(root);
        
        try {
            Document ret = (Document) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            return null;
        }
    }
    
    
    

}
