package org.sliit.web.api.util;

public class AccessFilter {

	public static boolean checkAccess(int[] giveAccess, String authString) {

		boolean status = false;
		Crypt c = new Crypt();

		if (authString != null) {
			for (int access : giveAccess) {

				int role = Integer.parseInt(c.decode(authString).getString("roleId"));

				if (role == access) {
					status = true;
				}

			}
		}
		return status;
	}

}
