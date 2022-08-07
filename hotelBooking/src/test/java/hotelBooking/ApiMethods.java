package hotelBooking;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static io.restassured.RestAssured.*;
import org.json.simple.JSONObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiMethods {

	
	public ApiMethods() {
		RestAssured.baseURI = "https://assign.free.beeceptor.com";
	}

	public String getAuth() throws FileNotFoundException, IOException, ParseException {

		Object obj = new JSONParser().parse(new FileReader("userCreds.json"));

		JSONObject credentials = (JSONObject) obj;
		String authToken = given().contentType("application/json").body(credentials).when().post("/auth").then()
				.statusCode(200).and().extract().jsonPath().getString("token");

		return authToken;

	}

	public Response healthCheck() {
		Response bookingresponse = given().when().get().then().extract().response();

		return bookingresponse;
	}

	public Response createBooking(JSONObject data1) {

		Response bookingresponse = given().contentType("application/json").accept("application/json").body(data1).when()
				.post("/booking").then().extract().response();

		return bookingresponse;
	}

	public Response getBookingByID(int bookingid) {

		Response bookingDetails = given().accept("application/json").basePath("/booking").pathParam("id", bookingid)
				.when().get("/{id}").then().extract().response();

		return bookingDetails;
	}

	public Response getBookingByFilter(HashMap param) {
		Response response = given().queryParams(param).when().get("/booking").then().extract().response();

		return response;

	}

	public Response partialUpdateBooking(String authToken, Map data, int sampleBookingId) {

		Response response = given().basePath("/booking").contentType("application/json").header("Cookie", authToken)
				.body(data).when().put("/{bookingId}", sampleBookingId).then().extract().response();

		return response;
	}

	public Response deleteBooking(String authToken, int sampleBookingId) {

		Response res = given().basePath("/booking").header("Cookie", authToken).when()
				.delete("/{bookingId}", sampleBookingId).then().extract().response();
		return res;
	}

	public Response getAllBookingIds() {

		Response response = given().when().get("/booking").then().statusCode(200).and().extract().response();

		return response;

	}

	public JSONObject getFileData(String filename) throws FileNotFoundException, IOException, ParseException {

		Object obj1 = new JSONParser().parse(new FileReader(filename));

		JSONObject data = (JSONObject) obj1;

		return data;

	}
}
