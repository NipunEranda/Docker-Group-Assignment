package org.sliit.web.api.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sliit.web.api.model.CommonResponse;

public interface UserService {

	// POST
	public CommonResponse registerUser(String firstName, String lastName, String initials, String dob, String phoneNo,
			String gender, String address, String email, String password);
	
	public CommonResponse userLogin(String email, String password);

	// PUT
	public CommonResponse updateUserDetails(String firstName, String lastName, String initials, String dob,
			String phoneNo, String gender, String address, String id);
	
	public CommonResponse resetPassword(String email, String currentPassword, String newPassword);

	// DELETE
	public CommonResponse deleteUser(String id);

	// GET
	public CommonResponse getUserDetails(String id);


}
