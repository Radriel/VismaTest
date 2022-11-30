package pageObjects;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import utils.GeneralUtils;
import utils.uiUtils.UITextFieldWrapper;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@DefaultUrl("https://forum.ghost.org/")
public class CommunityForum extends AbstractPage {

    @FindBy(id = "search-button")
    WebElementFacade btnSearch;

    @FindBy(xpath = ".//*[contains(@class,'show-advanced-search')]")
    WebElementFacade btnGoToAdvancedSearch;

    @FindBy(className = "search-menu-assistant-item")
    WebElementFacade btnSearchMenuAssistant;

    @FindBy(className = "results")
    WebElementFacade listOfSearchResults;

    @FindBy(id = "search-term")
    WebElementFacade tfdSearch;

    // Since the page URL is static we can use this check
    public boolean currentURLIsCorrect(){
        return getDriver().getCurrentUrl().equals(this.getDefaultURL());
    }

    @Step("Expand the Search form")
    public void expandTheSearchForm(){
        if(!tfdSearch.isCurrentlyVisible()){
            btnSearch.click();
        }
        tfdSearch.waitUntilVisible().and().waitUntilEnabled();
    }

    @Step("Collapse the Search form")
    public void collapseTheSearchForm(){
        if(tfdSearch.isCurrentlyVisible()){
            new Actions(getDriver())
                    .sendKeys(Keys.ESCAPE)
                    .perform();
        }
        tfdSearch.waitUntilNotVisible();
    }

    @Step("Execute a simple Search for {0}")
    public boolean executeSimpleSearch(String searchForText){
        expandTheSearchForm();
        if(!UITextFieldWrapper.insertText(tfdSearch, searchForText)){
            return false;
        }

        btnSearchMenuAssistant.waitUntilVisible().and().click();
        listOfSearchResults.waitUntilVisible();

        return true;
    }

    /**
     * Opens a result from the quick list of results that appear after a simple search
     * @return The URL of the random chosen item
     */
    @Step("Open random result from first displayed search results")
    public String openRandomSearchResult(){
        ListOfWebElementFacades items = listOfSearchResults.thenFindAll(".item");
        if(items.isEmpty()){
            log.error("The list of search results is empty");
            return GeneralUtils.NOT_SET_FLAG;
        }

        WebElementFacade chosenItem = items.get(ThreadLocalRandom.current().nextInt(0, items.size()));
        log.info("Open the topic with title '{}'", chosenItem.findBy(".topic-title>span").getTextContent());
        String result = chosenItem.findBy(".//*[contains(@class,'search-link')]").getAttribute("href");
        chosenItem.click();
        waitForLoadingSpinnerToDisappear();
        waitUntilExpectedURLIsLoaded(result);
        return result;
    }

    private String getDefaultURL(){
        return this.getClass().getAnnotation(DefaultUrl.class).value();
    }
}
