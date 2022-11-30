package utils.apiUtils;

import apiObjects.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.Serenity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.RestAssuredConfig.config;

@Slf4j
public abstract class EmployeeAPI {

    // In case of TOO MANY REQUESTS (resulting in 429) we might attempt to execute some calls.
    private static final int NUMBER_OF_ATTEMPTS = 5;

    public static final String BASE_URI = "https://dummy.restapiexample.com";
    public static final String EMPLOYEE_API_PATH = "/api/v1";
    public static final String EMPLOYEE_API_ALL_EMPLOYEES_ENDPOINT = "/employees";
    public static final String EMPLOYEE_API_SINGLE_EMPLOYEE_ENDPOINT = "/employee";
    public static final String EMPLOYEE_API_CREATE_EMPLOYEE_ENDPOINT = "/create";
    public static final String EMPLOYEE_API_UPDATE_EMPLOYEE_ENDPOINT = "/update";
    public static final String EMPLOYEE_API_DELETE_EMPLOYEE_ENDPOINT = "/delete";

    static {
        RestAssured.baseURI = BASE_URI;
        RestAssured.config = config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    /**
     * <b>Since the test platform is a public one, (I suspect) many people execute
     * REST calls on it, therefore we try the request a limited number of times
     * while the response code is 429</b>
     * @param id - give an ID if you want to execute GET <b>employee</b>; for GET <b>employees</b>
     *           do not put anything
     * @return The Response object of the GET Request
     */
    public static Response getSingleOrAllEmployees_Response_Avoid429(String... id){
        Response result = id.length > 0 ? getEmployee_Response(id[0]) : getAllEmployees_Response();
        if(result.getStatusCode() == 429){
            int tries = 0;
            do{
                result = id.length > 0 ? getEmployee_Response(id[0]) : getAllEmployees_Response();
                tries++;
            }while(result.getStatusCode() == 429 && tries < NUMBER_OF_ATTEMPTS);
        }

        return result;
    }

    /**
     * <b>Since the test platform is a public one, (I suspect) many people execute
     * REST calls on it, therefore we try the request a limited number of times
     * while the response code is 429</b>
     * @return The Response object of the POST Request
     */
    public static Response createEmployee_Response_Avoid429(Employee employee, Boolean... tryPUTInsteadOfPost) {
        Response result = tryPUTInsteadOfPost.length > 0 && tryPUTInsteadOfPost[0]
                ? createEmployeeThroughPUT_Response(employee)
                : createEmployee_Response(employee);
        if (result.getStatusCode() == 429) {
            int tries = 0;
            do {
                result = tryPUTInsteadOfPost.length > 0 && tryPUTInsteadOfPost[0]
                        ? createEmployeeThroughPUT_Response(employee)
                        : createEmployee_Response(employee);
                tries++;
            } while (result.getStatusCode() == 429 && tries < NUMBER_OF_ATTEMPTS);
        }

        return result;
    }

    /**
     * <b>Since the test platform is a public one, (I suspect) many people execute
     * REST calls on it, therefore we try the request a limited number of times
     * while the response code is 429</b>
     * @return The Response object of the PUT Request
     */
    public static Response updateEmployees_Response_Avoid429(Employee employee) {
        Response result = updateEmployee_Response(employee);
        if (result.getStatusCode() == 429) {
            int tries = 0;
            do {
                result = updateEmployee_Response(employee);
                tries++;
            } while (result.getStatusCode() == 429 && tries < NUMBER_OF_ATTEMPTS);
        }

        return result;
    }

    /**
     * <b>Since the test platform is a public one, (I suspect) many people execute
     * REST calls on it, therefore we try the request a limited number of times
     * while the response code is 429</b>
     * @return The Response object of the PUT Request
     */
    public static Response deleteEmployees_Response_Avoid429(String id) {
        Response result = deleteEmployee_Response(id);
        if (result.getStatusCode() == 429) {
            int tries = 0;
            do {
                result = deleteEmployee_Response(id);
                tries++;
            } while (result.getStatusCode() == 429 && tries < NUMBER_OF_ATTEMPTS);
        }

        return result;
    }

    /**
     * <b>To be used only when the response contains one employee</b>
     * @param response A Response containing one employee under <b>data</b> key
     * @return JSONObject with Employee data
     */
    public static JSONObject getEmployeeJSONObjectFromResponse(Response response){
        if(response.getStatusCode() == 200){
            JSONObject responseContent = getJSONObjectFromResponse(response);
            if(responseContent.has("data") && responseContent.get("data") != null) {
                return responseContent.getJSONObject("data");
            }else {
                log.error("Could not find the employee data in the response:");
            }

        }else{
            log.error("The response does not have status code 200:");
        }

        response.prettyPrint();
        return null;
    }

    /**
     * Execute a GET employee request for the given id and
     * instantiates an Employee object with the response data
     * @param id - the requested employee id
     * @return Retrieved Employee with the given id.
     */
    public static Employee getEmployee(String id){
        JSONObject employeeJSON = getEmployeeJSONObjectFromResponse(
                getSingleOrAllEmployees_Response_Avoid429(id)
        );
        if(employeeJSON.isEmpty()){
            return new Employee();
        }

        return new Employee(employeeJSON);
    }

    /**
     * Execute a GET employees request and generates a List
     * of Employee objects instantiated with the response data
     * @return List with all Employees from the response data.
     */
    public static List<Employee> getAllEmployees(){
        JSONArray employeesArray = getAllEmployeesJSONArray();
        List<Employee> result = new ArrayList<>();
        if(employeesArray == null || employeesArray.isEmpty()){
            return result;
        }

        for(int i = 0; i < employeesArray.length(); i++){
            result.add(new Employee(
                    employeesArray.getJSONObject(i)));
        }

        return result;
    }

    /**
     * Checks wheter or not at least one employee exists. If there is no employee,
     * then a new one will be created
     * @return false if no employee was found and the creation of a new one failed.
     */
    public static boolean makeSureAtLeastOneEmployeeExists(){
        if(getAllEmployees().isEmpty()){
            return Arrays.asList(200,201).contains(
                    createEmployee_Response_Avoid429(Employee.generateNewDummyEmployee()).getStatusCode()
            );
        }
        return true;
    }


    /**
     * Executes a GET employees request
     * @return Response for the GET request
     */
    private static Response getAllEmployees_Response(){
        Serenity.recordReportData().withTitle("Executing GET employees request");
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(EMPLOYEE_API_PATH + EMPLOYEE_API_ALL_EMPLOYEES_ENDPOINT)
                .then()
                .extract().response();
    }

    /**
     * Executes a GET employees request and returns a JSONArray
     * with JSONObject(s), each containing data from response
     * @return JSONArray with Employee data from response
     */
    private static JSONArray getAllEmployeesJSONArray(){
        Response response = getSingleOrAllEmployees_Response_Avoid429();
        if(response.getStatusCode() == 200){
            JSONObject responseContent = getJSONObjectFromResponse(response);
            if(responseContent.has("data")) {
                return responseContent.getJSONArray("data");
            }
            log.error("Could not find the employee data after successfully executing get employees call");
        }

        return null;
    }

    /**
     * Executes a GET employee request for the requested id
     * @param id - targeted employee id
     * @return Response for the GET request
     */
    private static Response getEmployee_Response(String id){
        Serenity.recordReportData().withTitle("Executing GET employee request")
                .andContents("GET employee with id " + id);
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(EMPLOYEE_API_PATH + EMPLOYEE_API_SINGLE_EMPLOYEE_ENDPOINT + "/" + id)
                .then()
                .extract().response();
    }

    /**
     * Executes a GET employee request for the requested id
     * @param id - targeted employee id
     * @return Response for the GET request
     */
    private static Response deleteEmployee_Response(String id){
        Serenity.recordReportData().withTitle("Executing GET employee request")
                .andContents("GET employee with id " + id);
        return given()
                .contentType(ContentType.JSON)
                .when()
                .delete(EMPLOYEE_API_PATH + EMPLOYEE_API_DELETE_EMPLOYEE_ENDPOINT + "/" + id)
                .then()
                .extract().response();
    }

    /**
     * Executes a PUT request to update an Employee.
     * <p><b>Be aware that the sent data is used from the given Employee object:
     *      <p>- the targeted employee to be updated is determined by the id from the given Employee</p>
     *      <p>- the new data is taken from the given employee</b></p></p>
     * @param employee - Employee containing new data and the id hat needs to be updated
     * @return Response for the PUT request
     */
    private static Response updateEmployee_Response(Employee employee){
        Serenity.recordReportData().withTitle("Executing PUT employee request")
                .andContents("Using data: " + employee);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(employee, new TypeReference<Map<String, Object>>() {
        });

        map.remove("id");

        return given()
                .contentType(ContentType.JSON)
                .formParams(map)
                .when()
                .put(EMPLOYEE_API_PATH + EMPLOYEE_API_UPDATE_EMPLOYEE_ENDPOINT + "/" + employee.id())
                .then()
                .extract().response();
    }

    private static Response createEmployee_Response(Employee employee){
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(employee, new TypeReference<Map<String, Object>>() {
        });

        map.remove("id");

        return given()
                .contentType(ContentType.JSON)
                .formParams(map)
                .when()
                .post(EMPLOYEE_API_PATH + EMPLOYEE_API_CREATE_EMPLOYEE_ENDPOINT)
                .then()
                .extract().response();
    }

    private static Response createEmployeeThroughPUT_Response(Employee employee){
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(employee, new TypeReference<Map<String, Object>>() {
        });

        map.remove("id");

        return given()
                .contentType(ContentType.JSON)
                .formParams(map)
                .when()
                .put(EMPLOYEE_API_PATH + EMPLOYEE_API_CREATE_EMPLOYEE_ENDPOINT + "/" + employee.id())
                .then()
                .extract().response();
    }

    /**
     * Returns JSONObject from given response.
     */
    private static JSONObject getJSONObjectFromResponse(Response response){
        String responseString = response.getBody().asString();
        JSONObject result = new JSONObject();
        try{
            result = new JSONObject(responseString);
        }catch(JSONException e){
            e.printStackTrace();
            log.error("Could not extract JSON object from the response:\n " + responseString);
        }
        return result;
    }

}
