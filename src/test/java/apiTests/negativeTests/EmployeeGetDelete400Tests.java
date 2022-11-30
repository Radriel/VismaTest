package apiTests.negativeTests;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import utils.apiUtils.EmployeeAPI;

import java.util.Arrays;
import java.util.Collection;

@Concurrent(threads = "4")
@RunWith(SerenityParameterizedRunner.class)
public class EmployeeGetDelete400Tests {

    // Parameterized attribute
    String invalidEmployeeID;

    public EmployeeGetDelete400Tests(String invalidEmployeeID){
        this.invalidEmployeeID = invalidEmployeeID;
    }

    // The test data passed to every test
    @TestData
    public static Collection<String> invalidEmployeeDataCombinations(){
        return Arrays.asList(
                "-1", // negative number
                new Faker().funnyName().name(), // alphabetical
                "A1", // alphanumeric
                "", // empty
                "001", // invalid int
                "!\"#$%&'()*+,‑./\\" // special characters
        );
    }

    @Test
    public void getEmployee_negative_invalidIDs(){
        Response getResponse_invalidID = EmployeeAPI.getSingleOrAllEmployees_Response_Avoid429(invalidEmployeeID);
        Serenity.reportThat("The response code for getting Employee using " + invalidEmployeeID
                        + " id is the expected one",
                () -> Assertions.assertThat(getResponse_invalidID.getStatusCode()).isEqualTo(400)
        );
    }

    @Test
    public void deleteEmployee_negative_invalidIDs(){
        Response getResponse_invalidID = EmployeeAPI.deleteEmployees_Response_Avoid429(invalidEmployeeID);
        Serenity.reportThat("The response code for deleting Employee using " + invalidEmployeeID
                        + " id is the expected one",
                () -> Assertions.assertThat(getResponse_invalidID.getStatusCode()).isEqualTo(400)
        );
    }


}
