package pageObjects;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class AbstractPage extends UIInteractionSteps {

    @FindBy(xpath = ".//*[contains(@class,'loading-container')]")
    WebElementFacade loadingSpinner;

    protected void waitForLoadingSpinnerToDisappear(){
        if(loadingSpinner.isCurrentlyVisible()){
            loadingSpinner.waitUntilNotVisible();
        }
    }

    protected void waitUntilExpectedURLIsLoaded(String expectedURL){
        waitFor(ExpectedConditions.urlToBe(expectedURL));
    }



}
