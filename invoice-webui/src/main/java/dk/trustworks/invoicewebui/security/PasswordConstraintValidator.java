package dk.trustworks.invoicewebui.security;


import org.passay.*;

import java.util.Arrays;

public class PasswordConstraintValidator {

    public boolean isValid(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(new LengthRule(8, 30)));

        RuleResult result = validator.validate(new PasswordData(password));
        return result.isValid();
    }
}
