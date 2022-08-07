package hotelBooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;

public class ApiTestCases {

	//Reusable variables
	ApiMethods methods = new ApiMethods();
	int testBookingId = 0;
	String Authcode = "";
	HashMap<String, String> sampleDataMap = new HashMap();

	@Before
	public void dataSetup() throws FileNotFoundException, IOException, ParseException {

		// health check
		Response res = methods.healthCheck();
		assertEquals(res.getStatusCode(), 201);

		// Authcode
		Authcode = methods.getAuth();
		System.out.println("auth code=" + Authcode);

		// Adding sample data
		JSONObject sampleDate = methods.getFileData("sampledata.json");
		sampleDataMap.putAll(sampleDate);
		res = methods.createBooking(sampleDate);

		assertEquals(res.statusCode(), 200);
		testBookingId = res.jsonPath().getInt("bookingid");
		assertNotNull(testBookingId);
	}

	@Test
	public void getBookingDetails() {

		Response res = methods.getAllBookingIds();

		// verify whether response has all booking id's

		assertNotNull(res.getBody().jsonPath().getInt("bookingid"));
		assertEquals(res.getStatusCode(), 200);

	}

	// @Test
	public void getFilteredBooking() {

		// passing the sample data value to filter the booking id

		HashMap<String, String> data = new HashMap();
		data.put("firstname", sampleDataMap.get("firstname"));
		data.put("lastname", sampleDataMap.get("lastname"));

		Response res = methods.getBookingByFilter(data);
		assertEquals(res.statusCode(), 200);

		// comparing the booking id in the response with booking id from datasetup
		int bookingId = res.getBody().jsonPath().getInt("bookingid");
		assertEquals(bookingId, testBookingId);
	}

	// @Test
	public void partialUpdateCheck() throws FileNotFoundException, IOException, ParseException {

		JSONObject data = methods.getFileData("updateData.json");

		Response res = methods.partialUpdateBooking(Authcode, data, testBookingId);

		int bookingId = res.jsonPath().getInt("bookingid");

		assertEquals(res.getStatusCode(), 200);

		res = methods.getBookingByID(bookingId);

		// Verifying the updated values
		String updatedFname = res.jsonPath().getString("firstname");
		String updatedLname = res.jsonPath().getString("lastname");

		assertEquals(updatedFname, data.get("firstname"));
		assertEquals(updatedLname, data.get("lastname"));

	}

	// @Test
	public void deleteSetupData() {

		Response res = methods.deleteBooking(Authcode, testBookingId);

		assertEquals(res.getStatusCode(), 201);

		res = methods.getBookingByID(testBookingId);

		assertEquals(res.statusCode(), 404);
	}

}
