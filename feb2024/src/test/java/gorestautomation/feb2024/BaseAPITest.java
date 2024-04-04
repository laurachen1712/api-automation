package gorestautomation.feb2024;

import static io.restassured.RestAssured.given;

import org.testng.annotations.BeforeMethod;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseAPITest {
	
	RequestSpecification loginJsonSpec;
	RequestSpecification loginJsonSpec2;
	RequestSpecification loginJsonSpec3;
	
	String token;
	
	// Using correct token
	@BeforeMethod
	public void login() {
		
		// maaf di hardcode karena tidak mendapat API documentation untuk login third party
		token = "Bearer 20fdaca719c68a6f9a52e9e1258c503f9d4d92902102f058b0fb06a1264ccb4d";
		
		loginJsonSpec = new RequestSpecBuilder().setBaseUri("https://gorest.co.in")
				.setContentType(ContentType.JSON).addHeader("Authorization", token).build().log().all();
		
		// Using wrong token
		token = "Bearer 0fdaca719c68a6f9a52e9e1258c503f9d4d92902102f058b0fb06a1264ccb4d";
		
		loginJsonSpec2 = new RequestSpecBuilder().setBaseUri("https://gorest.co.in")
				.setContentType(ContentType.JSON).addHeader("Authorization", token).build().log().all();
		
		// Not using token
		loginJsonSpec3 = new RequestSpecBuilder().setBaseUri("https://gorest.co.in")
				.setContentType(ContentType.JSON).build().log().all();
	}

}
