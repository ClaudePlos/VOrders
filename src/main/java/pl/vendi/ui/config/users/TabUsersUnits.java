/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.users;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.security.model.User;
import pl.vo.security.model.UsersCompanyUnits;
import pl.vo.security.model.UsersRoles;

/**
 *
 * @author Piotr
 */
public class TabUsersUnits extends HorizontalLayout {
    
      
    Table tabUnitsToAdd=  new Table("Obiekty do dodania");
    Table tabUnitsAdded = new Table("Obiekty nadane");

     // roles
    BeanItemContainer<OrganisationUnit> cntUnitsToAdd=  new BeanItemContainer<OrganisationUnit>(OrganisationUnit.class);
    BeanItemContainer<UsersCompanyUnits> cntUnitsAdded = new BeanItemContainer<UsersCompanyUnits>(UsersCompanyUnits.class);
   
    List<OrganisationUnit> listUnitsAll;
    
     WndConfigUsers parenWnd;
     
     User selectedUser; 
     
    public TabUsersUnits( WndConfigUsers parenWnd)
    {
        this.parenWnd = parenWnd;
         listUnitsAll = VOLookup.lookupOrganisationApi().findAll();
         
         this.addComponent(tabUnitsToAdd);
        this.addComponent(tabUnitsAdded);
        this.setSizeFull();
        tabUnitsAdded.setSizeFull();
        tabUnitsToAdd.setSizeFull();;
        this.setSpacing(true);

        tabUnitsAdded.setContainerDataSource(cntUnitsAdded);
        tabUnitsToAdd.setContainerDataSource(cntUnitsToAdd);

          tabUnitsToAdd.setSelectable(true);
        tabUnitsToAdd.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    BeanItem<OrganisationUnit> dvR = (BeanItem<OrganisationUnit>) event.getItem();
                    UsersCompanyUnits ur = new UsersCompanyUnits();
                    ur.setUser(selectedUser);
                    ur.setCompanyUnit(dvR.getBean());
                    selectedUser.getUnits().add(ur);

                    cntUnitsToAdd.removeItem(dvR.getBean());
                    cntUnitsAdded.addItem(ur);
                }
            }
        });

        tabUnitsAdded.setSelectable(true);
        tabUnitsAdded.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    BeanItem<UsersRoles> dvR = (BeanItem<UsersRoles>) event.getItem();
                    selectedUser.getRoles().remove(dvR.getBean());

                    cntUnitsToAdd.addItem(dvR.getBean().getRole());
                    cntUnitsAdded.removeItem(dvR.getBean());
                }
            }
        });

       
        cntUnitsAdded.addNestedContainerProperty("companyUnit.name");
        cntUnitsAdded.addNestedContainerProperty("companyUnit.code");
        tabUnitsAdded.setVisibleColumns(new Object[]{"companyUnit.code", "companyUnit.name"});
         tabUnitsToAdd.setVisibleColumns(new Object[]{"code", "name"});
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
        refreshRoles();
    }
    private void refreshRoles() {
        cntUnitsAdded.removeAllItems();
        cntUnitsToAdd.removeAllItems();

        if (selectedUser != null) {

            for (OrganisationUnit ou : listUnitsAll) {
                boolean exists = false;
                for (UsersCompanyUnits ur : selectedUser.getUnits()) 
                {
                    if ( ur.getCompanyUnit()!= null  ){ 
                    if (ur.getCompanyUnit().getId().equals(ou.getId())) {
                        exists = true;
                    }
                    }
                }

                if (!exists) {
                    cntUnitsToAdd.addItem(ou);
                }
            }

            cntUnitsAdded.addAll(selectedUser.getUnits());
        }
    }
}
