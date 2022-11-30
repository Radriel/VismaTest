package apiTests;

import apiObjects.Employee;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import utils.apiUtils.EmployeeAPI;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The checks in getAllEmployeesTest and getEmployeeTest are
 * done in other tests as well but still I've created them as well
 */
@RunWith(SerenityRunner.class)
public class EmployeesCRUDTests {

    @Test
    public void getAllEmployeesTest(){
        Serenity.reportThat("GET employees call works as expected",
                () -> Assertions.assertThat(
                        EmployeeAPI.getSingleOrAllEmployees_Response_Avoid429().getStatusCode()
                ).isEqualTo(200)
        );
    }

    @Test
    public void getEmployeeTest(){
        List<Employee> allEmployees = EmployeeAPI.getAllEmployees();
        Employee randomEmployee = allEmployees.get(ThreadLocalRandom.current().nextInt(0, allEmployees.size()));
        Employee requestEmployee = EmployeeAPI.getEmployee(randomEmployee.id());

        // Validate the EmployeeData
        Serenity.reportThat("GET employee retrieves the expected employee",
                () -> Assertions.assertThat(randomEmployee).isEqualTo(requestEmployee)
        );
    }

    @Test
    public void createEmployeeTest(){
        Employee employeeToCreate = Employee.generateNewDummyEmployee();

        // Execute and check Create
        Response response = EmployeeAPI.createEmployee_Response_Avoid429(employeeToCreate);
        Serenity.reportThat("Create employee call was executed successfully",
                () -> Assertions.assertThat(Arrays.asList(200,201)).contains(response.getStatusCode()) // 201 should be the retrieved code here, but we check 200 as well
        );

        String idOfNewEmployee = EmployeeAPI.getEmployeeJSONObjectFromResponse(response).getString("id");
        Serenity.recordReportData().withTitle("Employee was created successfully")
                .andContents("ID generated: " + idOfNewEmployee);

        // Validate the new created data
        Employee createdEmployee = EmployeeAPI.getEmployee(idOfNewEmployee);
        Serenity.reportThat("Response Data (except Employee ID) is the expected one ",
                () -> Assertions.assertThat(employeeToCreate.equalsExceptID(createdEmployee)).isTrue()
        );

        List<Employee> allEmployees = EmployeeAPI.getAllEmployees();

        Serenity.reportThat("The new employee can be found in all employees list",
                () -> Assertions.assertThat(allEmployees).contains(createdEmployee)
        );
    }

    // This might not be a valid scenario for this API, but I wrote it nonetheless.
    // Also, there is a lot of duplicated code here, need to be cleaned!
    @Test
    public void createEmployeeTestThroughPUT(){

        //Preconditions
        Employee employeeToCreate = Employee.generateNewDummyEmployee();
        List<Employee> allEmployees = EmployeeAPI.getAllEmployees();
        if(allEmployees.isEmpty()){
            employeeToCreate.id("1");
        }else{
            int newID = Integer.valueOf(allEmployees.get(allEmployees.size()-1).id()) + 1;
            employeeToCreate.id(String.valueOf(newID));

        }

        // Execute and check Create
        Response response = EmployeeAPI.createEmployee_Response_Avoid429(employeeToCreate, true);
        Serenity.reportThat("Create employee call was executed successfully",
                () -> Assertions.assertThat(Arrays.asList(200,201)).contains(response.getStatusCode()) // 201 should be the retrieved code here, but we check 200 as well
        );

        // Validate the new created data
        Employee createdEmployee = EmployeeAPI.getEmployee(employeeToCreate.id());
        Serenity.reportThat("Response Data (except Employee ID) is the expected one ",
                () -> Assertions.assertThat(employeeToCreate).isEqualTo(createdEmployee)
        );

        List<Employee> allEmployeesUpdated = EmployeeAPI.getAllEmployees();

        Serenity.reportThat("The new employee can be found in all employees list",
                () -> Assertions.assertThat(allEmployeesUpdated).contains(employeeToCreate)
        );
    }

    @Test
    public void updateEmployeeTest(){
        Employee employeeToUpdate;
        String idToUpdate;

        // Prerequisites
        Serenity.reportThat("Make sure at least one employee to be updated exists",
            () -> Assertions.assertThat(EmployeeAPI.makeSureAtLeastOneEmployeeExists()).isTrue()
        );
        List<Employee> employees = EmployeeAPI.getAllEmployees();
        idToUpdate = employees.get(ThreadLocalRandom.current().nextInt(0, employees.size())).id();
        employeeToUpdate = Employee.generateNewDummyEmployee().id(idToUpdate);

        // Execute and check Update
        Response response = EmployeeAPI.updateEmployees_Response_Avoid429(employeeToUpdate);

        Serenity.reportThat("Update employee call was executed successfully",
                () -> Assertions.assertThat(response.getStatusCode()).isEqualTo(200)
        );

        // Validate the new updated data
        Employee updatedEmployee = EmployeeAPI.getEmployee(employeeToUpdate.id());
        final Employee employeeToUpdate_final = employeeToUpdate; // Lambda expressions seems to be sensitive, so I had to declare a constant and use it

        Serenity.reportThat("The updated employee has the expected data",
                () -> Assertions.assertThat(employeeToUpdate_final).isEqualTo(updatedEmployee)
        );
    }

    @Test
    public void deleteEmployeeTest(){
        String idToDelete;
        Employee employeeToDelete;

        // Prerequisites
        Serenity.reportThat("Make sure at least one employee to be deleted exists",
                () -> Assertions.assertThat(EmployeeAPI.makeSureAtLeastOneEmployeeExists()).isTrue()
        );
        List<Employee> employees = EmployeeAPI.getAllEmployees();
        employeeToDelete = employees.get(ThreadLocalRandom.current().nextInt(0, employees.size()));
        idToDelete = employeeToDelete.id();

        // Execute and check Delete
        final Response deleteResponse = EmployeeAPI.deleteEmployees_Response_Avoid429(idToDelete);

        Serenity.reportThat("Delete employee call was executed successfully",
                () -> Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(200)
        );

        // Validate the deletion
        final Response getResponse = EmployeeAPI.getSingleOrAllEmployees_Response_Avoid429(idToDelete);
        Serenity.reportThat("The deleted employee cannot be retrieved",
                () -> Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(404)
        );

        List<Employee> allEmployees = EmployeeAPI.getAllEmployees();

        Serenity.reportThat("The deleted employee cannot be found in all employees list",
                () -> Assertions.assertThat(allEmployees).doesNotContain(employeeToDelete)
        );
    }

}
