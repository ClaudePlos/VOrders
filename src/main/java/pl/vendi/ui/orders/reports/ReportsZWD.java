/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.reports;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pl.vendi.ui.common.ComboBoxOrganisationUnit;
import pl.vendi.ui.common.DateFieldPl;
import pl.vo.common.model.ReportDTO;

/**
 *
 * @author k.skowronski
 */
public class ReportsZWD extends Window implements Button.ClickListener {
    
    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxAdd = new HorizontalLayout();

  
    public ReportsZWD()
    {
        super("Reports");
        this.setContent(vboxMain);
        
        vboxMain.setSpacing( true );
        vboxMain.setMargin( true );
        
        vboxMain.setSizeFull();
        
        hboxAdd.setSpacing(true); // Compact layout
        vboxMain.addComponent(hboxAdd);
        
        
        List<ReportDTO> raports = new ArrayList<>();
    
        raports.add( new ReportDTO(1, "Wykaz zamówień") );
        raports.add( new ReportDTO(2, "-") );
        raports.add( new ReportDTO(3, "-") );
        
    
        BeanItemContainer<ReportDTO> objects = new BeanItemContainer(ReportDTO.class, raports);
    
        ComboBox listReports = new ComboBox("1. Wybierz raport", objects);
        listReports.setItemCaptionPropertyId("name");
        listReports.setWidth("300px");
        
        
        listReports.addValueChangeListener(new ValueChangeListener() {
            
            public void valueChange(ValueChangeEvent event) {
                if (listReports.getValue() != null) {
                    
                ReportDTO val = (ReportDTO) listReports.getValue();
                
                if ( val.getName().equals("Wykaz zamówień") )
                {
                    ComboBoxOrganisationUnit cmbOrganisationUnit = new ComboBoxOrganisationUnit("Jednostka org");
                    hboxAdd.addComponent(cmbOrganisationUnit);
                }

             
                
                }
}
            
        });
                
        hboxAdd.addComponent(listReports);

        
        DateFieldPl dataOd = new DateFieldPl("2. Data od");
        hboxAdd.addComponent(dataOd);
        
        DateFieldPl dataDo = new DateFieldPl("3. Data do");
        hboxAdd.addComponent(dataDo);
        
        
        
        Button butRun = new Button("Uruchom");
        vboxMain.addComponent(butRun);
        
        
    }
    
    
    
    
    @Override
    public void buttonClick(Button.ClickEvent event) {

    }
  
    
}
