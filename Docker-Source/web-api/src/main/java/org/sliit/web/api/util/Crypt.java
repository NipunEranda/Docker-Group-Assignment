package org.sliit.web.api.util;

import java.util.Base64;
import org.json.JSONObject;

public class Crypt {

	// inputs
	private String confirmationCode;
	private long userId; // combinedUR = userId + roleId
	private String email;
	private long roleId;

	// Secret keys
	private final int secretNumber = 27182;
	private final String secretSymbolforCC = "=";
	private final String k = "spmsZysCey";
	private final String secretKey = "zpmz==";

	// Variables to support encode
	private String encodedCC;
	private String encodedCCNSK;
	private String combinedNumber;
	private String encodedAuthKey;

	// Variables to support decode
	private String decoded;
	private String[] components1;

	public Crypt() {}

	public Crypt(String confirmationCode, long userId, long roleId, String email) {
			this.confirmationCode = confirmationCode;
			this.userId = userId;
			this.roleId = roleId;
			this.email = email;
		}

	public String encode() {
		// Confirmation Code Encoder(Step1) (07613425)
		char[] ccCharSet = confirmationCode.toCharArray();
		String encodeString0 = "" + String.valueOf(ccCharSet[0]) + String.valueOf(ccCharSet[7])
				+ String.valueOf(ccCharSet[6]) + String.valueOf(ccCharSet[1]) + String.valueOf(ccCharSet[3])
				+ String.valueOf(ccCharSet[4]) + String.valueOf(ccCharSet[2]) + String.valueOf(ccCharSet[5])
				+ secretSymbolforCC;
		this.encodedCC = Base64.getEncoder().encodeToString(encodeString0.getBytes());
		// System.out.println(new String(Base64.getDecoder().decode(encodedCC)));

		// Combined number for advance security(Step2)
		combinedNumber = String.valueOf((secretNumber + userId + roleId)) + "u" + userId;

		// Combine and encrypt step1 & step2
		encodedCCNSK = Base64.getEncoder().encodeToString((encodedCC + "," + combinedNumber).toString().getBytes());

		// Combine email and security key + double encryption step3 (6374851902)
		String[] emailStringSplitter = email.split("@");
		String emailEncrypter = Base64.getEncoder().encodeToString((k + emailStringSplitter[0]).getBytes()) + ","
				+ Base64.getEncoder().encodeToString(emailStringSplitter[1].getBytes());
		String encodedString = Base64.getEncoder().encodeToString(emailEncrypter.getBytes());

		// 1st encryption
		encodedAuthKey = Base64.getEncoder().encodeToString((encodedCCNSK + "," + encodedString).getBytes());

		// 2nd encryption
		// encodedAuthKey =
		// Base64.getEncoder().encodeToString(encodedAuthKey.getBytes());

		// Add lastSecurityKey
		encodedAuthKey = encodedAuthKey.substring(0, 20) + secretKey
				+ encodedAuthKey.substring(20, encodedAuthKey.length());

		return encodedAuthKey;
	}

	public JSONObject decode(String encoded) {

		// Remove additional Security key
		encoded = encoded.replace(secretKey, "");

		// decode 1st time
		decoded = new String(Base64.getDecoder().decode(encoded));

		// decode 2nd time
		// decoded = new String(Base64.getDecoder().decode(decoded));

		// Split decoded value to 2 components ( 1 : Confirmation key with user details
		// 2 : email with k key )
		components1 = decoded.split(",");

		// Spliit and retrieve user email
		String emailEncryptionDec = new String(Base64.getDecoder().decode(components1[1]));
		this.email = (new String(Base64.getDecoder().decode(emailEncryptionDec.split(",")[0]))).replace(k, "") + "@"
				+ new String(Base64.getDecoder().decode(emailEncryptionDec.split(",")[1]));

		// Split and get other details
		String other = new String(Base64.getDecoder().decode(components1[0]));
		this.userId = Integer.parseInt(other.split(",")[1].split("u")[1]);
		this.roleId = Integer.parseInt(other.split(",")[1].split("u")[0]) - secretNumber - userId;

		// Retrieve confirmationCode
		String cc = (new String(Base64.getDecoder().decode(other.split(",")[0])));

		// Remove additional = from the code
		StringBuffer sb = new StringBuffer(cc);
		sb.deleteCharAt(sb.length() - 1);
		cc = sb.toString();

		char[] ccCharSet = cc.toCharArray();
		this.confirmationCode = "" + String.valueOf(ccCharSet[0]) + String.valueOf(ccCharSet[3])
				+ String.valueOf(ccCharSet[6]) + String.valueOf(ccCharSet[4]) + String.valueOf(ccCharSet[5])
				+ String.valueOf(ccCharSet[7]) + String.valueOf(ccCharSet[2]) + String.valueOf(ccCharSet[1]);

		JSONObject j = new JSONObject();
		j.put("email", this.email);
		j.put("userId", this.userId);
		j.put("roleId", this.roleId);
		j.put("confirmationCode", this.confirmationCode);

		return j;
	}

}
