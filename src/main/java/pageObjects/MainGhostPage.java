package pageObjects;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Step;

@DefaultUrl("https://ghost.org/")
public class MainGhostPage extends AbstractPage {

    // Not safe if the application supports multiple locales but
    //I didn't find an unique attribute
    @FindBy(linkText = "Community forum")
    WebElementFacade btnCommunityForum;

    //TODO: Add other elements as well

    @Step("Click on 'Community Forum'")
    public void goToCommunityForum(){
        this.btnCommunityForum.click();
    }

}
