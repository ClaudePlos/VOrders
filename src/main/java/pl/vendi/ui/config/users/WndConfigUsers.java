/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.users;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.ComboBoxCompany;
import pl.vendi.ui.common.ComboBoxDV;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.model.DocumentItem;
import pl.vo.security.api.UsersApi;
import pl.vo.security.model.User;
import pl.vo.security.model.UsersRoles;

/**
 *
 * @author Piotr
 */
public class WndConfigUsers extends Window {

    BeanContainer<Long, User> cntUsers = new BeanContainer<Long, User>(User.class);

    Table tblUsers = new Table("Użytkownicy");

    VerticalLayout vboxEdit = new VerticalLayout();

    TextField tfUsername = new TextField("Nazwa użytkownika");
    TextField tfFirstName = new TextField("Imię");
    TextField tfLastName = new TextField("Nazwisko");
    BeanItemContainer<DictionaryValue> cntUserTypes = new BeanItemContainer<DictionaryValue>(DictionaryValue.class);
    ComboBoxDV cmbType = new ComboBoxDV("Typ", cntUserTypes);
    CheckBox chkActive = new CheckBox("Aktywny");

    OptionGroup opType = new OptionGroup("Typ");
    ComboBoxCompany cmbCompany = new ComboBoxCompany("Powiązana firma");
    TextField tfRemoteToken = new TextField("TokenZdalny");

    HorizontalLayout hboxMain = new HorizontalLayout();

    UsersApi usersApi;

    private User selectedUser;

    TextField tfPassword = new TextField("Nowe hasło:");
    Button butChangePassword = new Button("Zmień hasło");

    VerticalLayout vboxLeft = new VerticalLayout();

    TabSheet tab = new TabSheet();
    TabUsersRoles tabRoles = new TabUsersRoles( this );
    TabUsersUnits tabUnits = new TabUsersUnits( this );
    
    public WndConfigUsers() {

        super("Konfiguracja użytkowników");

        usersApi = VOLookup.lookupUsersApi();

       

        this.setContent(hboxMain);
        hboxMain.setSizeFull();;
        hboxMain.setSpacing(true);
        hboxMain.setMargin(true);

        hboxMain.addComponent(vboxLeft);
        hboxMain.addComponent(vboxEdit);

        vboxLeft.addComponent(tblUsers);
        vboxLeft.addComponent(tab);
        
        tab.addTab(tabRoles,"Uprawnienia");
        tab.addTab(tabUnits,"Obiekty użytkownika");

        vboxLeft.setExpandRatio(tblUsers, 0.5f);
        vboxLeft.setExpandRatio(tab, 0.5f);
       // hboxRoles.setHeight("250px");
        vboxLeft.setSizeFull();
        tblUsers.setSizeFull();

        
        hboxMain.setExpandRatio(vboxLeft, 0.6f);
        hboxMain.setExpandRatio(vboxEdit, 0.3f);
        vboxEdit.setWidth("200px");
        
        // 

        tblUsers.setContainerDataSource(cntUsers);
        tblUsers.setWidth("100%");
        // create box edit
        cntUsers.setBeanIdProperty("id");
        cntUsers.addNestedContainerProperty("company.abbr");

        tblUsers.setSelectable(true);

        vboxEdit.addComponent(tfUsername);
        vboxEdit.addComponent(tfPassword);
        vboxEdit.addComponent(tfFirstName);
        vboxEdit.addComponent(tfLastName);
        vboxEdit.addComponent(cmbType);
        vboxEdit.addComponent(cmbCompany);
        vboxEdit.addComponent( tfRemoteToken );
        
        vboxEdit.addComponent(chkActive);

        tfUsername.setNullRepresentation("");
        tfPassword.setNullRepresentation("");
        tfFirstName.setNullRepresentation("");
        tfLastName.setNullRepresentation("");

        cntUserTypes.addItem(VO_UI_Consts.dvUserTypeInternal);
        cntUserTypes.addItem(VO_UI_Consts.dvUserTypeExternal);

        Button butAdd = new Button("Dodaj");
        Button butSave = new Button("Zapisz");

        vboxEdit.addComponent(butAdd);
        vboxEdit.addComponent(butSave);

        vboxEdit.addComponent(tfPassword);
        vboxEdit.addComponent(butChangePassword);

        vboxEdit.setSpacing(true);
        refreshUsers();

        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedUser = new User();
                modelToView();
            }
        });

        butSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickSave();
            }
        });

        butChangePassword.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                changePassword();
            }
        });

        tblUsers.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<User> biUser = (BeanItem<User>) event.getItem();

                selectedUser = biUser != null ? biUser.getBean() : null;
                modelToView();
            }
        });

      
        tblUsers.addGeneratedColumn("delete", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Button but = new Button();
                but.setCaptionAsHtml(true);
                but.setCaption(FontAwesome.TRASH_O.getHtml());
                but.addStyleName(ValoTheme.BUTTON_LINK);

                but.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {

                        removeItem((BeanItem<User>) tblUsers.getItem(itemId));
                    }
                });

                return but;

            }
        });

        tblUsers.setVisibleColumns(new String[]{"username", "firstName", "lastName", "active", "id", "type", "company.abbr", "delete"});

    }

    private void refreshUsers() {
        List<User> users = usersApi.findAllUsers();
        cntUsers.removeAllItems();
        cntUsers.addAll(users);

    }

    private void modelToView() {
        if (selectedUser != null) {
            tfLastName.setValue(selectedUser.getLastName());
            tfFirstName.setValue(selectedUser.getFirstName());
            tfUsername.setValue(selectedUser.getUsername());
            chkActive.setValue(selectedUser.getActive());
            cmbCompany.setValueCompany(selectedUser.getCompany());
            tfRemoteToken.setValue( selectedUser.getRemoteToken() );
            //tfPassword.setVisible( selectedUser.getId() == null );
            // tfPassword.setValue(null);
            cmbType.setDictionaryValue(selectedUser.getType());
        } else {
            tfLastName.setValue(null);
            tfFirstName.setValue(null);
            tfUsername.setValue(null);
            chkActive.setValue(null);
            chkActive.setValue(false);
            tfRemoteToken.setValue( null );
            // tfPassword.setVisible( false );
        }
        tabRoles.setSelectedUser( selectedUser );
        tabUnits.setSelectedUser( selectedUser) ;

    }

    private void viewToModel() {
        if (selectedUser != null) {
            selectedUser.setLastName(tfLastName.getValue());
            selectedUser.setFirstName(tfFirstName.getValue());
            selectedUser.setUsername(tfUsername.getValue());
            selectedUser.setActive(chkActive.getValue());
            selectedUser.setCompany(cmbCompany.getValueCompany());
            selectedUser.setRemoteToken( tfRemoteToken.getValue() );
            if (selectedUser.getActive() == null) {
                selectedUser.setActive(false);
            }

            if (selectedUser.getId() == null) {
                String pwd = tfPassword.getValue();
                selectedUser.setPassword(MD5(pwd));
            }

            DictionaryValue type = cmbType.getDictionaryValue();
            if (type != null) {
                selectedUser.setType(type.getValue());
            }

        }
    }

    private void changePassword() {

        if (selectedUser == null) {
            return;
        }

        if (tfPassword.getValue() == null || tfPassword.getValue().length() == 0) {
            Notification.show("Wprowadź nowe hasło", Notification.Type.ERROR_MESSAGE);
            return;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(tfPassword.getValue().getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            String generatedPasswordMD5 = sb.toString();

            selectedUser.setPassword(generatedPasswordMD5);
            save();
        } catch (NoSuchAlgorithmException nsa) {
            Notification.show("Nie udało się zakodować hasła", Notification.Type.ERROR_MESSAGE);
            return;
        }
    }

    private void save() {
        try {
            selectedUser = usersApi.save(selectedUser,VendiOrdersUI.getLoggedUsername());
        } catch (Exception wre) {
            VoExceptionHandler.handleException(wre);
            return;
        }

        boolean found = false;
        boolean eq = cntUsers.containsId(selectedUser.getId());
        if (eq) {
            int idx = cntUsers.indexOfId(selectedUser.getId());
            cntUsers.removeItem(selectedUser.getId());
            cntUsers.addItemAt(idx, selectedUser.getId(), selectedUser);
        } else {
            cntUserTypes.addItem(selectedUser);
        }
        /*   Iterator<User> it= cntUsers.getItemIds().iterator();
         while ( it.hasNext() ) {
         User u = (User) it.next();
         if ( u.getId().equals( selectedUser.getId()))
         it.remove();;
         }
         */
        /*
         for ( User us : cntUsers.getItemIds())
         {R
         if ( us.getId().equals( selectedUser.getId() ))
         {
         cntUsers.removeItem( us );
         }
         }*/

        //   cntUsers.addItem( selectedUser ); 
        // tblUsers.refreshRowCache();
        tblUsers.refreshRowCache();
        selectedUser = null;
        modelToView();
    }

    private void onClickSave() {

        if (selectedUser == null) {
            selectedUser = new User();
        }
        viewToModel();
        save();

    }

    private boolean contains(Long id) {
        /*for ( User us : cntUsers.getItemIds()){
         if ( us.getId().equals( id))
         return true; 
         }*/
        return false;
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    

    private void removeItem(BeanItem<User> item) {
        try {
            usersApi.delete(item.getBean());
        } catch (Exception wre) {
            VoExceptionHandler.handleException(wre);
            return;
        }

        refreshUsers();
    }
}
