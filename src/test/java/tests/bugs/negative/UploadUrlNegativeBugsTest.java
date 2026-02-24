package tests.bugs.negative;

import baseApi.BaseApiTest;
import baseApi.constants.Constants;
import baseApi.models.request.UploadUrlRequest;
import org.testng.annotations.Test;

import static baseApi.constants.ErrorMessages.PLEASE_PROVIDE_VALID_URL;

public class UploadUrlNegativeBugsTest extends BaseApiTest {

	/**
	 * N2 bug report check Test will fail because the message is not as described in
	 * the document
	 */
	@Test(description = "Returns 400 if the url is invalid")
	public void uploadUrlInvalidUrlTest() {
		String invalidUrl = "setvi.com";

		UploadUrlRequest body = new UploadUrlRequest(invalidUrl, 3, 0.5);

		uploadUrl(Constants.API_KEY, body)
				.verifyStatusCode(400)
				.verifyMessageEquals(PLEASE_PROVIDE_VALID_URL);
	}

	/**
	 * N2 bug report check positive case
	 */
	@Test(description = "Returns 200 if the url is valid")
	public void uploadUrlValidUrlTest() {
		String validUrl = "https://www.setvi.com";

		UploadUrlRequest body = new UploadUrlRequest(validUrl, 3, 0.5);

		uploadUrl(Constants.API_KEY, body)
				.verifyStatusCode(200);
	}

}
