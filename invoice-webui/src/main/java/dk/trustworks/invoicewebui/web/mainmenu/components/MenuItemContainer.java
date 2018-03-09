package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.vaadin.server.FontIcon;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import dk.trustworks.invoicewebui.model.RoleType;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.label.MLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 05/09/2017.
 */
public class MenuItemContainer {

    protected static Logger logger = LoggerFactory.getLogger(MenuItemImpl.class.getName());

    private RoleType[] roles;
    private List<MenuItemContainer> childItems;
    private MenuItemContainer parent;
    private ResponsiveColumn menuItemColumn;

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

    public ResponsiveColumn getMenuItemColumn() {
        return menuItemColumn;
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

    public MenuItemContainer createItem(String caption, boolean isTitle, FontIcon icon, String nagivateTo, boolean isChild, RoleType... roleTypes) {
        ResponsiveColumn menuItemColumn = new ResponsiveColumn();
        roles = roleTypes;
        child = isChild;
        Component menuItem;
        // If title
        if(isTitle) {
            menuItem = new MLabel("     "+caption).withStyleName("dark-grey-font dark-grey-icon");
        } else {
            menuItem = new MenuItemImpl()
                    .withCaption(caption)
                    .withIcon(icon)
                    .withFontStyle("grey-font grey-icon")
                    .setChild(isChild);
            ((MenuItemImpl)menuItem).addClickListener(event -> {
                Sentry.getContext().recordBreadcrumb(
                        new BreadcrumbBuilder().setMessage(nagivateTo).build()
                );
                UI.getCurrent().getNavigator().navigateTo(nagivateTo);
            });
        }

        menuItemColumn
                .withDisplayRules(12,12,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(menuItem);
        this.menuItemColumn = menuItemColumn;

        return this;
    }

    public void foldInOut() {
        System.out.println("MenuItemContainer.foldInOut");
        ((MenuItemImpl) getMenuItemColumn().getComponent()).withParentIndicator(MenuItemImpl.MINUS_INDICATOR);
        for (MenuItemContainer itemContainer : getChildItems()) {
            itemContainer.getMenuItemColumn().getComponent().setVisible(!itemContainer.getMenuItemColumn().getComponent().isVisible());
        }
    }

    public void foldOut() {
        System.out.println("MenuItemContainer.foldOut");
        ((MenuItemImpl) getMenuItemColumn().getComponent()).withParentIndicator(MenuItemImpl.MINUS_INDICATOR);
        for (MenuItemContainer itemContainer : getChildItems()) {
            itemContainer.getMenuItemColumn().getComponent().setVisible(true);
        }
    }

    public RoleType[] getRoles() {
        return roles;
    }

    public boolean isChild() {
        return child;
    }
}
