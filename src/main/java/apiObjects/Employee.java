package apiObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.*;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import utils.GeneralUtils;

import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public class Employee {

    // This should be int; was set to String for negative testing
    // At first I thought the id should never
    // be changed after initialization
//    @Setter(AccessLevel.NONE)
    private String id = GeneralUtils.NOT_SET_FLAG;

    @JsonProperty("employee_name")
    private String fullName = GeneralUtils.NOT_SET_FLAG;

    // This should be int; was set to String for negative testing
    @JsonProperty("employee_age")
    private String age = GeneralUtils.NOT_SET_FLAG;

    // This should be long; was set to String for negative testing
    @JsonProperty("employee_salary")
    private String salary = GeneralUtils.NOT_SET_FLAG;

    @JsonProperty("profile_image")
    private String profileImage = GeneralUtils.NOT_SET_FLAG;

    public Employee (JSONObject employee){
        this(employee.getString("id"),
                employee.getString("employee_name"),
                employee.getString("employee_age"),
                employee.getString("employee_salary"),
                employee.getString("profile_image"));
    }

    /**
     * Verifies if this is equal with the given Employee object, <b>regardless of the ID</b>
     */
    public boolean equalsExceptID(Employee employee){
        return (this.fullName.equals(employee.fullName())
                && this.age == employee.age()
                && this.salary == employee.salary()
                && this.profileImage.equals(employee.profileImage()));
    }

    public static Employee generateNewDummyEmployee(){
        return new Employee("-1",
                new Faker().name().fullName(),
                String.valueOf(ThreadLocalRandom.current().nextInt(20, 50)),
                String.valueOf(ThreadLocalRandom.current().nextInt(2000, 5000)),
                "");
    }

    @Override
    public String toString(){
        return "Employee '" + this.fullName + "' with id '" + this.id + "', age " + this.age + " and salary " + this.salary;
    }

}
