package api;

import base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Helper;

import java.util.*;

public class ApiTest extends BaseApiTest {

    private String bookingId;
    private HashMap<String, Object> bookingEntry;

    @BeforeClass
    public void ping() {
        Response response = get("ping");
        assertStatus(response, 201);
    }

    @Test
    public void bookingPostTest() {
        HashMap<String, Object> bookingDates = new HashMap<>();
        bookingDates.put("checkin", Helper.getDate(0));
        bookingDates.put("checkout",Helper.getDate(1));
        HashMap<String, Object> booking = new HashMap<>();
        booking.put("bookingdates", bookingDates);
        booking.put("firstname", "John");
        booking.put("lastname", "Smith");
        booking.put("totalprice", 123);
        booking.put("depositpaid", true);
        booking.put("additionalneeds", "Early checkin");

        Response response = post("booking", booking);
        assertStatus(response, 200);

        Assert.assertNotNull(response.path("bookingid"), "booking ID should not be empty");
        //for dependent tests
        bookingId = response.path("bookingid").toString();
        bookingEntry = booking;
    }

    @Test(dependsOnMethods = "bookingPostTest")
    public void bookingGetTest() {
        Response response = get("booking");
        assertStatus(response, 200);
        Assert.assertNotNull(response.path(""),"response should not be NULL");
        Assert.assertTrue(response.path("") instanceof List<?>, "response should contain a list of bookings");
        Assert.assertTrue((!((ArrayList<?>) response.path("")).isEmpty()), "received list should not be empty");
        Assert.assertTrue(response.path("").toString().contains(bookingId), "booking list should include earlier stored booking ID");
    }

    @Test(dependsOnMethods = {"bookingPostTest"})
    public void bookingGetIdTest() {
        Response response = get("booking/" + bookingId);
        assertStatus(response, 200);
        Assert.assertEquals(response.path(""), bookingEntry, "fetched booking is not equal to earlier stored one");
    }

    @Test(dependsOnMethods = {"bookingPostTest"})
    public void madeToFailTest() {
        Response response = post("booking/" + bookingId, bookingEntry);
        assertStatus(response, 200);
    }
}
