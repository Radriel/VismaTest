package uiTests;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import pageObjects.CommunityForum;
import pageObjects.MainGhostPage;
import utils.GeneralUtils;

@RunWith(SerenityRunner.class)
public class SearchForumTests {

    @Managed
    WebDriver driver;

    MainGhostPage mainPage;
    CommunityForum forumPage;

    @Test
    public void searchAndOpenRandomBlog(){
        String textToSearch = "create new blog";

        mainPage.open();
        mainPage.goToCommunityForum();

        // Optional check, we can remove the URL assertion
        Serenity.reportThat("The Forum Page URL is correct",
                () -> Assertions.assertThat(forumPage.currentURLIsCorrect()).isTrue()
        );

        Serenity.reportThat("Executing a Search for '" + textToSearch + "' works",
                () -> Assertions.assertThat(forumPage.executeSimpleSearch(textToSearch)).isTrue()
        );

        String openedTopicURL = forumPage.openRandomSearchResult();

        Serenity.reportThat("A random result could be found after searching for '" + textToSearch + "'",
                () -> Assertions.assertThat(openedTopicURL).isNotEqualTo(GeneralUtils.NOT_SET_FLAG)
        );

        Serenity.reportThat("The opened page is the expected one",
                () -> Assertions.assertThat(driver.getCurrentUrl()).isEqualTo(openedTopicURL)
        );
    }

}
