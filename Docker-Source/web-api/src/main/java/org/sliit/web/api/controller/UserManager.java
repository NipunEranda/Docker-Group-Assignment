package org.sliit.web.api.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.json.JSONObject;
import org.sliit.web.api.model.CommonResponse;
import org.sliit.web.api.services.UserService;
import org.sliit.web.api.services.UserServiceImp;
import org.sliit.web.api.util.AccessFilter;
import org.sliit.web.api.util.CorsFilter;
import org.sliit.web.api.util.Crypt;

import com.google.gson.Gson;

@Path("/api/user")
public class UserManager {

	UserService us = new UserServiceImp();

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CommonResponse registerUser(@RequestBody String data) {

		JSONObject j = new JSONObject(data);
		return us.registerUser(j.getString("firstName"), j.getString("lastName"), j.getString("initials"),
				j.getString("dob"), j.getString("phoneNo"), j.getString("gender"), j.getString("address"),
				j.getString("email"), j.getString("password"));
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CommonResponse login(@RequestBody String data) {
		JSONObject j = new JSONObject(data);
		return us.userLogin(j.getString("email"), j.getString("password"));
	}

	@PUT
	@Path("/resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CommonResponse resetPassword(@RequestBody String data) {
		JSONObject j = new JSONObject(data);
		return us.resetPassword(j.getString("email"), j.getString("currentPassword"), j.getString("newPassword"));
	}

	@PUT
	@Path("/updateUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public CommonResponse updateUser(@RequestBody String data, @HeaderParam(value = "authString") String authString) {
		int[] access = { 2 };
		JSONObject j = new JSONObject(data);
		CommonResponse cr = new CommonResponse();
		if (AccessFilter.checkAccess(access, authString)) {

			cr = us.updateUserDetails(String.valueOf(new Crypt().decode(authString).getString("userId")),
					j.getString("firstName"), j.getString("lastName"), j.getString("initials"), j.getString("dob"),
					j.getString("phoneNo"), j.getString("gender"), j.getString("address"));

			return cr;
		} else {
			cr.defaultAccessDeniedMessage();
			return cr;
		}
	}

	@DELETE
	@Path("/deleteUser/{uid}")
	@Produces(MediaType.APPLICATION_JSON)
	public CommonResponse deleteUser(@PathParam("uid") String uid,
			@HeaderParam(value = "authString") String authString) {
		int[] access = { 1 };
		CommonResponse cr = new CommonResponse();
		if (AccessFilter.checkAccess(access, authString)) {
			cr = us.deleteUser(uid);
			return cr;
		} else {
			cr.defaultAccessDeniedMessage();
			return cr;
		}
	}

	@GET
	@Path("/{uid}")
	@Produces(MediaType.APPLICATION_JSON)
	public CommonResponse getUserDetails(@PathParam("uid") String uid,
			@HeaderParam(value = "authString") String authString) {
		return us.getUserDetails(uid);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Hello RESTEasy";
	}
}
