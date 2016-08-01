/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.events;

import java.io.Serializable;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class DocumentChangedEvent  implements Serializable
{
    
    private  Document document; 

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public DocumentChangedEvent(Document document) {
        this.document = document;
    }
    
    
    
    
    
}
