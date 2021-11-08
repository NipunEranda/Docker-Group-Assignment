package org.sliit.web.api.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sliit.web.api.model.CommonResponse;
import org.sliit.web.api.model.Role;
import org.sliit.web.api.model.User;
import org.sliit.web.api.util.Crypt;
import org.sliit.web.api.util.DBConnection;
import org.sliit.web.api.util.RandomCode;
import org.sliit.web.api.util.SQLQueryManager;
import org.sliit.web.api.util.lambdaworks.crypto.SCryptUtil;

public class DBManager {

	private static SQLQueryManager sqlObject;
	private static DBManager dbObject;

	private DBManager() {
	}

	public static DBManager getInstance() {
		if (dbObject == null) {
			synchronized (DBManager.class) {
				if (dbObject == null) {
					dbObject = new DBManager();
					sqlObject = SQLQueryManager.getInstance();
				}
			}
		}
		return dbObject;
	}

	private static JSONObject checkEmailAvailability(String email) {
		JSONObject j = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			con = DBConnection.connect();
			ps = con.prepareStatement(sqlObject.getUSER_CHECKEMAILAVAILABILITY());
			ps.setString(1, email);
			rs = ps.executeQuery();
			if (rs != null) {
				if (rs.first()) {
					j.put("status", "error");
					j.put("reason", "email already exist!");
				} else {
					j.put("status", "success");
				}
			}

		} catch (Exception e) {
			j.put("status", "error");
			System.out.println(e.getMessage());
		}
		return j;
	}

	static class LoginClass {

		public static CommonResponse login(String email, String password) {
			JSONObject j = new JSONObject();
			CommonResponse cr = new CommonResponse();
			PreparedStatement ps_verifyLogin = null;
			ResultSet rs_verifyLogin = null;
			Connection con = null;
			CallableStatement stm = null;

			try {
				con = DBConnection.connect();
				ps_verifyLogin = con.prepareStatement(sqlObject.getLOGIN_VERIFY());
				ps_verifyLogin.setString(1, email);

				rs_verifyLogin = ps_verifyLogin.executeQuery();
				if (rs_verifyLogin.first() != false && SCryptUtil.check(password, rs_verifyLogin.getString(5))) {
					if (rs_verifyLogin.getInt(6) == 1) {

						Crypt ed = new Crypt(rs_verifyLogin.getString(7), rs_verifyLogin.getLong(3),
								rs_verifyLogin.getLong(2), email);
						String authString = ed.encode();

						j.put("status", "success");
						j.put("loginId", rs_verifyLogin.getLong(1));
						j.put("loginRole", rs_verifyLogin.getLong(2));
						j.put("userId", rs_verifyLogin.getLong(3));
						j.put("firstName", rs_verifyLogin.getString(4));
						j.put("loginCount", rs_verifyLogin.getInt(8) + 1);
						j.put("authString", authString);

						stm = con.prepareCall(sqlObject.getINCREASE_LOGIN_COUNT());
						stm.setInt(1, rs_verifyLogin.getInt(1));
						stm.registerOutParameter(2, Types.INTEGER);
						stm.execute();

						cr.defaultSuccessMessage(j.toString());

					} else {
						cr.defaultErrorMessage();
					}
				} else {
					cr.defaultErrorMessage();
				}
			} catch (Exception e) {
				cr.defaultExceptionMessage(e);
			}

			return cr;
		}

		private static JSONObject verifyPassword(String email, String currentPassword) {

			JSONObject j = new JSONObject();

			Connection con = null;
			PreparedStatement ps_verifyPassword = null;

			try {

				con = DBConnection.connect();
				ps_verifyPassword = con.prepareStatement(sqlObject.getVERIFY_PASSWORD());
				ps_verifyPassword.setString(1, email);

				ResultSet rs_verifyPassword = ps_verifyPassword.executeQuery();
				if (rs_verifyPassword != null) {
					if (rs_verifyPassword.first() != false
							&& SCryptUtil.check(currentPassword, rs_verifyPassword.getString(1))) {
						j.put("status", "success");
					} else {
						j.put("status", "error");
					}
				} else {
					System.out.println("Email doesn't exists");
					j.put("status", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return j;
		}

		public static CommonResponse resetPassword(String email, String currentPassword, String newPassword) {
			CommonResponse cr = new CommonResponse();
			Connection con = null;
			CallableStatement stm = null;
			try {
				con = DBConnection.connect();
				JSONObject passwordVerification = verifyPassword(email, currentPassword);
				if (passwordVerification.getString("status").equalsIgnoreCase("success")) {
					stm = con.prepareCall(sqlObject.getCHANGE_PASSWORD());
					stm.setString(1, SCryptUtil.scrypt(newPassword, 16, 16, 16));
					stm.setString(2, email);
					stm.registerOutParameter(3, Types.INTEGER);
					stm.execute();
					if (stm.getInt(3) == 1) {
						cr.defaultSuccessMessage(null);
					} else {
						cr.defaultErrorMessage();
					}
				} else {
					cr.defaultErrorMessage();
				}

			} catch (Exception e) {
				cr.defaultExceptionMessage(e);
			}
			return cr;
		}
	}

	static class UserClass {

		public static CommonResponse registerUser(String firstName, String lastName, String initials, String dob,
				String phoneNo, String gender, String address, String email, String password) {
			CommonResponse cr = new CommonResponse();
			Connection con = null;
			CallableStatement stm;

			try {
				if (checkEmailAvailability(email).getString("status").equalsIgnoreCase("success")) {
					con = DBConnection.connect();
					stm = con.prepareCall(sqlObject.getUSER_REGISTRATION());
					String generatedSecuredPasswordHash = SCryptUtil.scrypt(password, 16, 16, 16);
					String code = RandomCode.GenCode(8);
					stm.setString(1, firstName);
					stm.setString(2, lastName);
					stm.setString(3, initials);
					stm.setString(4, dob);
					stm.setString(5, phoneNo);
					stm.setString(6, gender);
					stm.setString(7, address);
					stm.setString(8, email);
					stm.setString(9, generatedSecuredPasswordHash);
					stm.setLong(10, Long.parseLong("3"));
					stm.setString(11, code);
					stm.registerOutParameter(12, Types.INTEGER);
					stm.execute();
					if (stm.getInt(12) == 1) {
						cr.defaultSuccessMessage(null);
					} else {
						cr.defaultErrorMessage();
					}
				} else {
					cr.setResponse("400", "email already exist");
				}

			} catch (Exception e) {
				cr.defaultExceptionMessage(e);
			}

			return cr;
		}

		public static CommonResponse updateUserDetails(String id, String firstName, String lastName, String initials,
				String dob, String phoneNo, String gender, String address) {
			CommonResponse cr = new CommonResponse();
			Connection con = null;
			CallableStatement stm = null;

			try {

				con = DBConnection.connect();
				stm = con.prepareCall(sqlObject.getUPDATE_USER_DETAILS());
				stm.setString(1, firstName);
				stm.setString(2, lastName);
				stm.setString(3, initials);
				stm.setString(4, dob);
				stm.setString(5, phoneNo);
				stm.setString(6, gender);
				stm.setString(7, address);
				stm.setLong(8, Long.parseLong(id));
				stm.registerOutParameter(9, Types.INTEGER);
				stm.execute();
				if (stm.getInt(9) == 1) {
					cr.defaultSuccessMessage(null);
				} else {
					cr.defaultErrorMessage();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return cr;
		}

		public static CommonResponse getUserById(String id) {
			CommonResponse cr = new CommonResponse();
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			User u;

			try {
				con = DBConnection.connect();
				ps = con.prepareStatement(sqlObject.getUSER_GETUSERBYID());
				ps.setLong(1, Long.parseLong(id));
				rs = ps.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						u = new User(id, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
								rs.getString(6), rs.getString(7), rs.getString(8));
						cr.defaultSuccessMessage(new JSONObject(u).toString());
					}
				} else {
					cr.defaultErrorMessage();
				}

			} catch (Exception e) {
				e.printStackTrace();
				cr.defaultExceptionMessage(e);
			}
			return cr;
		}

		public static CommonResponse deleteUser(String id) {
			CommonResponse cr = new CommonResponse();
			Connection con = null;
			CallableStatement stm = null;

			try {
				con = DBConnection.connect();
				stm = con.prepareCall(sqlObject.getUSER_DELETEUSER());
				stm.setLong(1, Long.parseLong(id));
				stm.registerOutParameter(2, Types.INTEGER);
				stm.execute();
				if (stm.getInt(2) == 1) {
					cr.defaultSuccessMessage(null);
				} else {
					cr.defaultErrorMessage();
				}

			} catch (Exception e) {
				cr.defaultExceptionMessage(e);
			}
			return cr;
		}
	}
}
