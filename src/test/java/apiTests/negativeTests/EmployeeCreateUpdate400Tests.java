package apiTests.negativeTests;

import apiObjects.Employee;
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
public class EmployeeCreateUpdate400Tests {

    // Parameterized attribute
    Employee testEmployee;

    public EmployeeCreateUpdate400Tests(Employee testEmployee){
        this.testEmployee = testEmployee;
    }

    // The test data passed to every test
    @TestData
    public static Collection<Employee> invalidEmployeeDataCombinations(){
        return Arrays.asList(
                new Employee(),// Every attribute has the NOT_SET_FLAG value
                Employee.generateNewDummyEmployee().fullName(""), // Empty name
                Employee.generateNewDummyEmployee().fullName("-1"), // Numerical name
                Employee.generateNewDummyEmployee().fullName("!\"#$%&'()*+,‑./\\"), // Special characters name
                Employee.generateNewDummyEmployee().age("-1"), // Negative age
                Employee.generateNewDummyEmployee().age(new Faker().funnyName().name()), // Alphabetical age
                Employee.generateNewDummyEmployee().age("!\"#$%&'()*+,‑./\\"), // Special characters age
                Employee.generateNewDummyEmployee().salary("-1"), // Negative salary
                Employee.generateNewDummyEmployee().salary(new Faker().funnyName().name()), // Alphabetical salary
                Employee.generateNewDummyEmployee().salary("!\"#$%&'()*+,‑./\\")); // Special characters salary
    }

    @Test
    public void createEmployee_negative_invalidEmployeeData(){
        Response getResponse_invalidEmployee = EmployeeAPI.createEmployee_Response_Avoid429(testEmployee);
        Serenity.reportThat("The response code for creating invalid " + testEmployee
                        + " is the expected one",
                () -> Assertions.assertThat(getResponse_invalidEmployee.getStatusCode()).isEqualTo(400)
        );
    }

    @Test
    public void updateEmployee_negative_invalidEmployeeData(){
        Response getResponse_invalidEmployee = EmployeeAPI.updateEmployees_Response_Avoid429(testEmployee);
        Serenity.reportThat("The response code for updating invalid " + testEmployee
                        + " is the expected one",
                () -> Assertions.assertThat(getResponse_invalidEmployee.getStatusCode()).isEqualTo(400)
        );
    }

}
