package apiTests.negativeTests;

import apiObjects.Employee;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.junit.annotations.Concurrent;
import net.thucydides.junit.annotations.TestData;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import utils.apiUtils.EmployeeAPI;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Concurrent(threads = "2")
@RunWith(SerenityParameterizedRunner.class)
public class Employee404Tests {

    String validButNonExistentID;

    public Employee404Tests(String validButNonExistentID){
        this.validButNonExistentID = validButNonExistentID;
    }

    @TestData
    public static String establishNonExistentID(){
        List<Employee> employees = EmployeeAPI.getAllEmployees();
        if(employees.isEmpty()){
            return "1";
        }

        return String.valueOf(
                Integer.valueOf(employees.get(employees.size()-1).id()) + ThreadLocalRandom.current().nextInt(5, 10)
        );
    }

    @Test
    public void getEmployee_negative_IDNotFound(){
        Response getResponse_nonExistentID = EmployeeAPI.getSingleOrAllEmployees_Response_Avoid429(validButNonExistentID);
        Serenity.reportThat("The response code for getting Employee using " + validButNonExistentID
                        + " id (which is expected to not be found) is the expected one",
                () -> Assertions.assertThat(getResponse_nonExistentID.getStatusCode()).isEqualTo(404)
        );
    }

    @Test
    public void deleteEmployee_negative_IDNotFound(){
        Response getResponse_nonExistentID = EmployeeAPI.deleteEmployees_Response_Avoid429(validButNonExistentID);
        Serenity.reportThat("The response code for deleting Employee using " + validButNonExistentID
                        + " id (which is expected to not be found) is the expected one",
                () -> Assertions.assertThat(getResponse_nonExistentID.getStatusCode()).isEqualTo(404)
        );
    }

    @Test
    public void updateEmployee_negative_IDNotFound(){
        Response getResponse_nonExistentID = EmployeeAPI.updateEmployees_Response_Avoid429(Employee.generateNewDummyEmployee().id(validButNonExistentID));
        Serenity.reportThat("The response code for updating Employee using " + validButNonExistentID
                        + " id (which is expected to not be found) is the expected one",
                () -> Assertions.assertThat(getResponse_nonExistentID.getStatusCode()).isEqualTo(404)
        );
    }

}
