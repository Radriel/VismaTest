package utils.uiUtils;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.pages.WebElementFacade;

/**
 * This class is used as a wrapper over TextField type elements, to avoid copy/pasting
 * code for basic actions whenever a textfield is interacted with.
 */
@Slf4j
public abstract class UITextFieldWrapper {

    public static boolean insertText(WebElementFacade uiTextField, String text){
        uiTextField.clear();
        uiTextField.sendKeys(text);
        if(!uiTextField.getValue().equals(text)){
            log.warn("Inserted text does not match after attempting to insert text '{}'", text);
            return false;
        }
        return true;
    }

    public static boolean appendText(WebElementFacade uiTextField, String text){
        String initialText = uiTextField.getValue();
        uiTextField.sendKeys(text);
        if(!uiTextField.getValue().equals(initialText + text)){
            log.warn("Inserted text does not match after attempting to append text '{}'", text);
            return false;
        }
        return true;
    }

}
