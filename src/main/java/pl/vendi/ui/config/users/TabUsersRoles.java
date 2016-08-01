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
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.security.model.User;
import pl.vo.security.model.UsersRoles;

/**
 *
 * @author Piotr
 */
public class TabUsersRoles extends HorizontalLayout
{
    
    
    
    
    Table tabRolesToAdd = new Table("Uprawnienia do dodania");
    Table tabRolesAdded = new Table("Uprawnienia nadane");

     // roles
    BeanItemContainer<DictionaryValue> cntRolesToAdd = new BeanItemContainer<DictionaryValue>(DictionaryValue.class);
    BeanItemContainer<UsersRoles> cntRolesAdded = new BeanItemContainer<UsersRoles>(UsersRoles.class);
    HorizontalLayout hboxRoles = new HorizontalLayout();
   
    List<DictionaryValue> listRolesAll;
    
     WndConfigUsers parenWnd;
     
     User selectedUser; 
     
    public TabUsersRoles( WndConfigUsers parenWnd)
    {
        hboxRoles = this; 
        this.parenWnd = parenWnd;
         listRolesAll = VOLookup.lookupDictionaryApi().listByDictionaryCode(VO_UI_Consts.DICTIONARY_CODE_ROLES);
         
         hboxRoles.addComponent(tabRolesToAdd);
        hboxRoles.addComponent(tabRolesAdded);
        hboxRoles.setSizeFull();
        tabRolesAdded.setSizeFull();
        tabRolesToAdd.setSizeFull();;
        hboxRoles.setSpacing(true);

        tabRolesAdded.setContainerDataSource(cntRolesAdded);
        tabRolesToAdd.setContainerDataSource(cntRolesToAdd);

          tabRolesToAdd.setSelectable(true);
        tabRolesToAdd.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    BeanItem<DictionaryValue> dvR = (BeanItem<DictionaryValue>) event.getItem();
                    UsersRoles ur = new UsersRoles();
                    ur.setUser(selectedUser);
                    ur.setRole(dvR.getBean());
                    ur.setUsername( selectedUser.getUsername());
                    ur.setRolename( dvR.getBean().getValue() );
                    selectedUser.getRoles().add(ur);

                    cntRolesToAdd.removeItem(dvR.getBean());
                    cntRolesAdded.addItem(ur);
                }
            }
        });

        tabRolesAdded.setSelectable(true);
        tabRolesAdded.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    BeanItem<UsersRoles> dvR = (BeanItem<UsersRoles>) event.getItem();
                    selectedUser.getRoles().remove(dvR.getBean());

                    cntRolesToAdd.addItem(dvR.getBean().getRole());
                    cntRolesAdded.removeItem(dvR.getBean());
                }
            }
        });

        tabRolesToAdd.setVisibleColumns(new Object[]{"value", "description"});
        cntRolesAdded.addNestedContainerProperty("role.value");
        cntRolesAdded.addNestedContainerProperty("role.description");
        tabRolesAdded.setVisibleColumns(new Object[]{"role.value", "role.description"});

    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
        refreshRoles();
    }
    private void refreshRoles() {
        cntRolesAdded.removeAllItems();
        cntRolesToAdd.removeAllItems();

        if (selectedUser != null) {

            for (DictionaryValue dvRol : listRolesAll) {
                boolean exists = false;
                for (UsersRoles ur : selectedUser.getRoles()) 
                {
                    if ( ur.getRole() != null  ){ 
                    if (ur.getRole().getValue().equals(dvRol.getValue())) {
                        exists = true;
                    }
                    }
                }

                if (!exists) {
                    cntRolesToAdd.addItem(dvRol);
                }
            }

            cntRolesAdded.addAll(selectedUser.getRoles());
        }
    }
    
}


