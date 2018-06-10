package dk.trustworks.invoicewebui.security;


import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

import java.util.Collections;

public class PasswordConstraintValidator {

    public boolean isValid(String password) {
        PasswordValidator validator = new PasswordValidator(Collections.singletonList(new LengthRule(8, 30)));

        RuleResult result = validator.validate(new PasswordData(password));
        return result.isValid();
    }
}
