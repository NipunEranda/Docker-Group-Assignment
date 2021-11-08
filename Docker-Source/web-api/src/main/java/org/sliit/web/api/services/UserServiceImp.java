package org.sliit.web.api.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sliit.web.api.model.CommonResponse;

public class UserServiceImp implements UserService {

	DBManager db = DBManager.getInstance();
	
	@Override
	public CommonResponse resetPassword(String email, String currentPassword, String newPassword) {
		return DBManager.LoginClass.resetPassword(email, currentPassword, newPassword);
	}

	@Override
	public CommonResponse registerUser(String firstName, String lastName, String initials, String dob, String phoneNo,
			String gender, String address, String email, String password) {
		return DBManager.UserClass.registerUser(firstName, lastName, initials, dob, phoneNo, gender, address, email, password);
	}

	@Override
	public CommonResponse userLogin(String email, String password) {
		return DBManager.LoginClass.login(email, password);
	}

	@Override
	public CommonResponse updateUserDetails(String firstName, String lastName, String initials, String dob, String phoneNo,
			String gender, String address, String id) {
		return DBManager.UserClass.updateUserDetails(id, firstName, lastName, initials, dob, phoneNo, gender, address);
	}

	@Override
	public CommonResponse deleteUser(String id) {
		return DBManager.UserClass.deleteUser(id);
	}

	@Override
	public CommonResponse getUserDetails(String id) {
		return DBManager.UserClass.getUserById(id);
	}

}
