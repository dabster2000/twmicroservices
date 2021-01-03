package dk.trustworks.invoicewebui.web.project.components;

import com.vaadin.ui.ComboBox;
import dk.trustworks.invoicewebui.functions.TokenEventListener;

import java.util.ArrayList;
import java.util.List;

public class TokenListImpl extends TokenList {

    private final List<String> selectableValues;
    private final List<String> tokens;
    private final List<TokenEventListener> listeners = new ArrayList<>();
    private String placeholder = "add offering";

    public TokenListImpl() {
        tokens = new ArrayList<>();
        selectableValues = new ArrayList<>();
        addComponent(createComboBox());
    }

    public TokenListImpl(List<String> selectableValues, List<String> selectedValues) {
        this.selectableValues = selectableValues;
        tokens = selectedValues;
        addTokenLabels();
    }

    public TokenListImpl(List<String> selectableValues, List<String> selectedValues, String placeholder) {
        this(selectableValues, selectedValues);
        this.placeholder = placeholder;
    }

    public void setSelectableValues(List<String> selectableValues) {
        this.selectableValues.clear();
        this.selectableValues.addAll(selectableValues);
    }

    public void addToken(String token) {
        this.tokens.add(token);
        addTokenLabels();
    }

    public void addTokens(List<String> tokens) {
        this.tokens.addAll(tokens);
        addTokenLabels();
    }

    private void addTokenLabels() {
        removeAllComponents();
        for (String token : tokens) {
            Token tokenItem = new Token();
            tokenItem.getBtnDeleteOffering().setCaption(token);
            tokenItem.getBtnDeleteOffering().addClickListener(event -> {
                tokens.remove(token);
                addTokenLabels();
                notifyTokenRemovedListeners(token);
            });
            addComponent(tokenItem);
        }
        addComponent(createComboBox());
    }

    public List<String> getTokens() {
        return tokens;
    }

    private ComboBox<String> createComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(selectableValues);
        comboBox.addStyleName("floating");
        comboBox.addStyleName("tiny");
        comboBox.setPlaceholder(placeholder);
        comboBox.setTextInputAllowed(true);
        comboBox.addValueChangeListener(event -> {
            addToken(event.getValue());
            notifyTokenAddedListeners(event.getValue());
        });
        return comboBox;
    }

    public void addTokenListener(TokenEventListener tokenEventListener) {
        this.listeners.add(tokenEventListener);
    }

    protected void notifyTokenAddedListeners (String token) {
        this.listeners.forEach(listener -> listener.onTokenAdded(token));
    }

    protected void notifyTokenRemovedListeners (String token) {
        this.listeners.forEach(listener -> listener.onTokenRemoved(token));
    }
}