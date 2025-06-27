package api;

import api.data.Register;
import api.data.SuccessReg;
import api.data.UserData;
import api.spec.Specifications;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String URL = "https://reqres.in/";
    @Test
    public void checkAvatarAndIdTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

//        users.stream().forEach(x -> {
//            Assert.assertNotNull(x.getAvatar(), "Avatar is null");
//            Assert.assertTrue(x.getAvatar().contains(x.getId().toString()));
//        });
//
       Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatar = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());
        for(int i = 0; i<avatar.size(); i++){
            Assert.assertTrue(avatar.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);

        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());

    }

    @Test
    public void getListUsersTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());

        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then()
                .log().all()
                .extract().jsonPath().getList("data", UserData.class);

        Assert.assertEquals(users.size(), 6, "Expected 6 users");

        // Перевірка email
        for (UserData user : users) {
            Assert.assertTrue(user.getEmail().contains("@reqres.in"), "Email does not contain @reqres.in");
        }
    }

    @Test
    public void getSingleUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());

        UserData user = given()
                .when()
                .get("api/users/2")
                .then()
                .log().all()
                .extract().jsonPath().getObject("data", UserData.class);

        Assert.assertTrue(user.getFirst_name().contains("Janet"), "Name does not contain 'Janet'");
    }

    @Test
    public void getSingleUserNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec404());

        String responseBody = given()
                .when()
                .get("api/users/23")
                .then()
                .log().all()
                .extract().asString();

        Assert.assertEquals(responseBody, "{}", "Response body is not empty");
    }


}
