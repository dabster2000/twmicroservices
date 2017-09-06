package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.vaadin.server.FontIcon;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 05/09/2017.
 */
public class MenuItemContainer {

    protected static Logger logger = LoggerFactory.getLogger(MenuItemImpl.class.getName());

    private List<MenuItemContainer> childItems;
    private MenuItemContainer parent;
    private ResponsiveColumn menuItem;

    private int order;
    private boolean child;

    public MenuItemContainer(int order) {
        this.order = order;
        childItems = new ArrayList<>();
    }

    public int getOrder() {
        return order;
    }

    public List<MenuItemContainer> getChildItems() {
        return childItems;
    }

    public ResponsiveColumn getMenuItem() {
        return menuItem;
    }

    public MenuItemContainer setParentMenuItem(MenuItemContainer parent) {
        this.parent = parent;
        parent.addChildMenuItemContainer(this);
        return this;
    }

    public MenuItemContainer getParent() {
        return parent;
    }

    private void addChildMenuItemContainer(MenuItemContainer child) {
        this.childItems.add(child);
    }

    public MenuItemContainer createItem(String caption, FontIcon parentIndicator, FontIcon icon, String nagivateTo, boolean isChild) {
        child = isChild;
        ResponsiveColumn menuItemColumn = new ResponsiveColumn();
        MenuItemImpl menuItem = new MenuItemImpl()
                .withCaption(caption)
                .withParentIndicator(parentIndicator)
                .withIcon(icon)
                .setChild(isChild);
        if(parentIndicator==null) menuItem.addClickListener(event -> UI.getCurrent().getNavigator().navigateTo(nagivateTo));
        menuItemColumn
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem);
        this.menuItem = menuItemColumn;
        if(parentIndicator != null) {
            ((MenuItemImpl) getMenuItem().getComponent()).addClickListener(event -> foldOut());
        }
        return this;
    }

    public void foldOut() {
        ((MenuItemImpl) getMenuItem().getComponent()).withParentIndicator(MenuItemImpl.MINUS_INDICATOR);
        for (MenuItemContainer itemContainer : getChildItems()) {
            itemContainer.getMenuItem().getComponent().setVisible(!itemContainer.getMenuItem().getComponent().isVisible());
        }
    }

    public boolean isChild() {
        return child;
    }
}
