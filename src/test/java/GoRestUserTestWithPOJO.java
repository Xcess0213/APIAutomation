import POJO.GoRestUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUserTestWithPOJO {
    private RequestSpecification reqSpec;
    private GoRestUser user;

    @BeforeClass
    public void setup(){

        RestAssured.baseURI = "https://gorest.co.in";

        reqSpec = given()
                .log().body()
                .header("Authorization","Bearer df67db584c764e803d2a0a18f665ee09a4a456373dbe8fd736fbfbf367ba44f7")
                .contentType(ContentType.JSON);

        user = new GoRestUser();
        user.setName("Johnson Paula");
        user.setEmail("Johnsonpaula@gmail.com");
        user.setGender("male");
        user.setStatus("active");
    }

    @Test
    public void createUserTest(){

        user.setId(given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(201)
                .body("name", equalTo(user.getName()))
                .extract().jsonPath().getString("id"));
    }

    @Test(dependsOnMethods = "createUserTest")
    public void createUserNegativeTest() {
        given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(422);
    }

    @Test
    public void getUserTest() {
        given()
                .spec(reqSpec)
                .when()
                .get("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(user.getName()))
                .body("email", equalTo(user.getEmail()))
                .body("gender", equalTo(user.getGender()))
                .body("status", equalTo(user.getStatus()));
    }

    @Test(dependsOnMethods = "getUserTest")
    public void updateUserTest(){
        HashMap<String, String> body = new HashMap<>();
        body.put("status", "inactive");

        given()
                .spec(reqSpec)
                .body(body)
                .when()
                .put("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("status", equalTo(body.get("status")));
    }

    @Test(dependsOnMethods = "updateUserTest")
    public void deleteUserTest() {
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteUserTest")
    public void deleteUserNegativeTest() {
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(404);
    }
}
