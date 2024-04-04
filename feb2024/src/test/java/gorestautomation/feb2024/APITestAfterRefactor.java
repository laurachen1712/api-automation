package gorestautomation.feb2024;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import gorestautomation.feb2024.utils.DataUtility;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class APITestAfterRefactor extends BaseAPITest {
	
	String unregisteredUserId = "6733725"; 	
	
	@Test
	// Create new user with valid format
	public void createNewUserAPI() {
		
		String payload = "{\"name\":\"Sevena\",\"email\":\"sevena@gmail.com\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseCreateNewUser = given().spec(loginJsonSpec).body(payload).when()
				.post("/public/v2/users");
		
		String expectedName = "Sevena";
		String expectedEmail = "sevena@gmail.com";
		String expectedGender = "female";
		String expectedStatus = "active";
				
		// butuh json schema untuk cek id kosong atau tidak
		
		assertEquals(responseCreateNewUser.statusCode(), 201);
		assertEquals(responseCreateNewUser.jsonPath().get("name"), expectedName);
		assertEquals(responseCreateNewUser.jsonPath().get("email"), expectedEmail);
		assertEquals(responseCreateNewUser.jsonPath().get("gender"), expectedGender);
		assertEquals(responseCreateNewUser.jsonPath().get("status"), expectedStatus);

	}
	
	@Test
	// Create new user with empty value
	public void createNewUserAPI2() {
		
		String payload = "{\"name\":\"\",\"email\":\"\",\"gender\":\"\",\"status\":\"\"}";

	
		Response responseCreateNewUser = given().spec(loginJsonSpec).body(payload).when()
				.post("/public/v2/users");
		
		String expectedField0 = "email";
		String expectedField1 = "name";
		String expectedField2 = "gender";
		String expectedField3 = "status";
		
		String expectedMessage0 = "can't be blank";
		String expectedMessage1 = "can't be blank";
		String expectedMessage2 = "can't be blank, can be male of female";
		String expectedMessage3 = "can't be blank";
		
		assertEquals(responseCreateNewUser.statusCode(), 422);
		
		assertEquals(responseCreateNewUser.jsonPath().get("[0].field"), expectedField0);
		assertEquals(responseCreateNewUser.jsonPath().get("[1].field"), expectedField1);
		assertEquals(responseCreateNewUser.jsonPath().get("[2].field"), expectedField2);
		assertEquals(responseCreateNewUser.jsonPath().get("[3].field"), expectedField3);
		

		assertEquals(responseCreateNewUser.jsonPath().get("[0].message"), expectedMessage0);
		assertEquals(responseCreateNewUser.jsonPath().get("[1].message"), expectedMessage1);
		assertEquals(responseCreateNewUser.jsonPath().get("[2].message"), expectedMessage2);
		assertEquals(responseCreateNewUser.jsonPath().get("[3].message"), expectedMessage3);

	}
	
	@Test
	// Create new user with invalid email format
	public void createNewUserAPI3() {
		
		String payload = "{\"name\":\"Siti\",\"email\":\"siti\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseCreateNewUser = given().spec(loginJsonSpec).body(payload).when()
				.post("/public/v2/users");
		
		String expectedField0 = "email";
		
		String expectedMessage0 = "is invalid";
		
		assertEquals(responseCreateNewUser.statusCode(), 422);
		
		assertEquals(responseCreateNewUser.jsonPath().get("[0].field"), expectedField0);

		assertEquals(responseCreateNewUser.jsonPath().get("[0].message"), expectedMessage0);
		
	}
	
	@Test
	// Create new user with already existing email
	public void createNewUserAPI4() {
		
		String payload = "{\"name\":\"Siti\",\"email\":\"nicolechen@gmail.com\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseCreateNewUser = given().spec(loginJsonSpec).body(payload).when()
				.post("/public/v2/users");
		
		String expectedField0 = "email";
		
		String expectedMessage0 = "has already been taken";
		
		assertEquals(responseCreateNewUser.statusCode(), 422);
		
		assertEquals(responseCreateNewUser.jsonPath().get("[0].field"), expectedField0);

		assertEquals(responseCreateNewUser.jsonPath().get("[0].message"), expectedMessage0);
		
	}
	
	@Test
	// Create new user with valid format using wrong token
	public void createNewUserAPI5() {
		
		String payload = "{\"name\":\"Fanuel Feliks\",\"email\":\"fanuelfeliks@gmail.com\",\"gender\":\"male\",\"status\":\"active\"}";

	
		Response responseCreateNewUser = given().spec(loginJsonSpec2).body(payload).when()
				.post("/public/v2/users");
		
		String expectedMessage = "Invalid token";
				
		assertEquals(responseCreateNewUser.statusCode(), 401);
		
		assertEquals(responseCreateNewUser.jsonPath().get("message"), expectedMessage);
		
	}
	
	@Test
	// Create new user with valid format using no token
	public void createNewUserAPI6() {
		
		String payload = "{\"name\":\"Fanuel Feliks\",\"email\":\"fanuelfeliks@gmail.com\",\"gender\":\"male\",\"status\":\"active\"}";

	
		Response responseCreateNewUser = given().spec(loginJsonSpec3).body(payload).when()
				.post("/public/v2/users");
		
		String expectedMessage = "Authentication failed";
				
		assertEquals(responseCreateNewUser.statusCode(), 401);
		
		assertEquals(responseCreateNewUser.jsonPath().get("message"), expectedMessage);
		
	}
	
	@Test
	// Get existing user detail
	public void getUserDetailAPI() {
	
//	    Response responseGetUserDetail = given().spec(loginJsonSpec).when().get("/public/v2/users/6819733").then()
//	    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getuserdetail.json")).extract().response();
		
		Response responseGetUserDetail = given().spec(loginJsonSpec).contentType(ContentType.JSON).when().get("/public/v2/users/6819733");
		
		Integer expectedId = 6819733;
		String expectedName = "Siti";
		String expectedEmail = "nicolechen@gmail.com";
		String expectedGender = "female";
		String expectedStatus = "active";
				
		assertEquals(responseGetUserDetail.statusCode(), 200);
		
		assertEquals(responseGetUserDetail.jsonPath().get("id"), expectedId);
		assertEquals(responseGetUserDetail.jsonPath().get("name"), expectedName);
		assertEquals(responseGetUserDetail.jsonPath().get("email"), expectedEmail);
		assertEquals(responseGetUserDetail.jsonPath().get("gender"), expectedGender);
		assertEquals(responseGetUserDetail.jsonPath().get("status"), expectedStatus);
		
	    responseGetUserDetail.then().assertThat().body(matchesJsonSchema(DataUtility.getDataFromExcel("Schemas", "getuserdetail")));

	}

	@Test
	// Get non existing user detail
	public void getUserDetailAPI2() { 

//		Response responseGetUserDetail = given().spec(loginJsonSpec).when().get("/public/v2/users/" + unregisteredUserId).then()
//			    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("message.json")).extract().response();
		
		Response responseGetUserDetail = given().spec(loginJsonSpec).contentType(ContentType.JSON).when().get("/public/v2/users/" + unregisteredUserId);
		
		String expectedMessage = "Resource not found";
				
		assertEquals(responseGetUserDetail.statusCode(), 404);
		
		assertEquals(responseGetUserDetail.jsonPath().get("message"), expectedMessage);
		
		responseGetUserDetail.then().assertThat().body(matchesJsonSchema(DataUtility.getDataFromExcel("Schemas", "message")));
		
	}
	
	@Test
	// Get existing user detail using wrong token
	public void getUserDetailAPI3() {
		
//		Response responseGetUserDetail = given().spec(loginJsonSpec2).when().get("/public/v2/users/6819733").then()
//			    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("message.json")).extract().response();
			
		Response responseGetUserDetail = given().spec(loginJsonSpec2).contentType(ContentType.JSON).when().get("/public/v2/users/6819733");
		
		String expectedMessage = "Invalid token";
				
		assertEquals(responseGetUserDetail.statusCode(), 401);
		
		assertEquals(responseGetUserDetail.jsonPath().get("message"), expectedMessage);
		
		responseGetUserDetail.then().assertThat().body(matchesJsonSchema(DataUtility.getDataFromExcel("Schemas", "message")));
		
	}
	
	@Test
	// Get existing user detail using no token
	public void getUserDetailAPI4() {

//		Response responseGetUserDetail = given().spec(loginJsonSpec3).when().get("/public/v2/users/6819733").then()
//			    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("message.json")).extract().response();
		
		Response responseGetUserDetail = given().spec(loginJsonSpec3).contentType(ContentType.JSON).when().get("/public/v2/users/6819733");
		
		String expectedMessage = "Resource not found";
				
		assertEquals(responseGetUserDetail.statusCode(), 404);
		
		assertEquals(responseGetUserDetail.jsonPath().get("message"), expectedMessage);
		
		responseGetUserDetail.then().assertThat().body(matchesJsonSchema(DataUtility.getDataFromExcel("Schemas", "message")));
		
	}
	
	@Test
	// Update user with valid format
	public void updateUserDetailsAPI() {
		
		String payload = "{\"name\":\"Samuel Feliks\",\"email\":\"samuelfeliks@gmail.com\",\"gender\":\"male\",\"status\":\"active\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec).body(payload).when()
				.put("/public/v2/users/6821642");
		
		String expectedName = "Samuel Feliks";
		String expectedEmail = "samuelfeliks@gmail.com";
		String expectedGender = "male";
		String expectedStatus = "active";
				
		// butuh json schema untuk cek id kosong atau tidak
		
		assertEquals(responseUpdateUserDetails.statusCode(), 200);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("name"), expectedName);
		assertEquals(responseUpdateUserDetails.jsonPath().get("email"), expectedEmail);
		assertEquals(responseUpdateUserDetails.jsonPath().get("gender"), expectedGender);
		assertEquals(responseUpdateUserDetails.jsonPath().get("status"), expectedStatus);

	}
	
	@Test
	// Update non existing user
	public void updateUserDetailsAPI2() {
		
		String payload = "{\"name\":\"Samuel Feliks\",\"email\":\"samuelfeliks@gmail.com\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec).body(payload).when()
				.put("/public/v2/users/" + unregisteredUserId);
		
		String expectedMessage = "Resource not found";
		
		assertEquals(responseUpdateUserDetails.statusCode(), 404);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("message"), expectedMessage);
		
	}
	
	@Test
	// Update user with empty value
	public void updateUserDetailsAPI3() {
		
		String payload = "{\"name\":\"\",\"email\":\"\",\"gender\":\"\",\"status\":\"\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec).body(payload).when()
				.put("/public/v2/users/6821477");
		
		String expectedField0 = "email";
		String expectedField1 = "name";
		String expectedField2 = "gender";
		String expectedField3 = "status";
		
		String expectedMessage0 = "can't be blank";
		String expectedMessage1 = "can't be blank";
		String expectedMessage2 = "can't be blank, can be male of female";
		String expectedMessage3 = "can't be blank";
		
		assertEquals(responseUpdateUserDetails.statusCode(), 422);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("[0].field"), expectedField0);
		assertEquals(responseUpdateUserDetails.jsonPath().get("[1].field"), expectedField1);
		assertEquals(responseUpdateUserDetails.jsonPath().get("[2].field"), expectedField2);
		assertEquals(responseUpdateUserDetails.jsonPath().get("[3].field"), expectedField3);
		

		assertEquals(responseUpdateUserDetails.jsonPath().get("[0].message"), expectedMessage0);
		assertEquals(responseUpdateUserDetails.jsonPath().get("[1].message"), expectedMessage1);
		assertEquals(responseUpdateUserDetails.jsonPath().get("[2].message"), expectedMessage2);
		assertEquals(responseUpdateUserDetails.jsonPath().get("[3].message"), expectedMessage3);
		
	}
	
	@Test
	// Update user with already existing email
	public void updateUserDetailsAPI4() {
		
		String payload = "{\"name\":\"Suneo\",\"email\":\"nicolechen@gmail.com\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec).body(payload).when()
				.put("/public/v2/users/6821477");
		
		String expectedField0 = "email";
		
		String expectedMessage0 = "has already been taken";
		
		assertEquals(responseUpdateUserDetails.statusCode(), 422);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("[0].field"), expectedField0);

		assertEquals(responseUpdateUserDetails.jsonPath().get("[0].message"), expectedMessage0);
		
	}
	
	@Test
	// Update user with invalid email format
	public void updateUserDetailsAPI5() {
		
		String payload = "{\"name\":\"Siti\",\"email\":\"siti\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec).body(payload).when()
				.put("/public/v2/users/6821476");
		
		String expectedField0 = "email";
		
		String expectedMessage0 = "is invalid";
		
		assertEquals(responseUpdateUserDetails.statusCode(), 422);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("[0].field"), expectedField0);

		assertEquals(responseUpdateUserDetails.jsonPath().get("[0].message"), expectedMessage0);
		
	}
	
	@Test
	// Update user with valid format using wrong token
	public void updateUserDetailsAPI6() {
		
		String payload = "{\"name\":\"Samuel Feliks\",\"email\":\"samuelfeliks@gmail.com\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec2).body(payload).when()
				.put("/public/v2/users/6743804");
		
		String expectedMessage = "Invalid token";
		
		assertEquals(responseUpdateUserDetails.statusCode(), 401);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("message"), expectedMessage);
	
	}
	
	@Test
	// Update user with valid format using no token
	public void updateUserDetailsAPI7() {
		
		String payload = "{\"name\":\"Samuel Feliks\",\"email\":\"samuelfeliks@gmail.com\",\"gender\":\"female\",\"status\":\"active\"}";

	
		Response responseUpdateUserDetails = given().spec(loginJsonSpec3).body(payload).when()
				.put("/public/v2/users/6743804");
		
		String expectedMessage = "Resource not found";
		
		assertEquals(responseUpdateUserDetails.statusCode(), 404);
		
		assertEquals(responseUpdateUserDetails.jsonPath().get("message"), expectedMessage);

	}
	
	@Test
	// Delete user
	public void deleteUserAPI() {
	
		Response responseDeleteUser = given().spec(loginJsonSpec).when()
				.delete("/public/v2/users/6821475");
			
		assertEquals(responseDeleteUser.statusCode(), 204);
		
		// maaf tidak ada assert error message karena tidak ada error messagenya
		
	}
	
	@Test
	// Delete non existing user
	public void deleteUserAPI2() {
	
		Response responseDeleteUser = given().spec(loginJsonSpec).when()
				.delete("/public/v2/users/" + unregisteredUserId);
		
		String expectedMessage = "Resource not found";
				
		assertEquals(responseDeleteUser.statusCode(), 404);
		
		assertEquals(responseDeleteUser.jsonPath().get("message"), expectedMessage);
		
	}
	
}
