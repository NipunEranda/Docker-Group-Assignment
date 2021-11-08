var API_URL = "http://0.0.0.0:9999/api/";

//User Login Function
function userLogin(email, password) {
    document.getElementById("loader").classList.remove("hidden");
    $.ajax({
        type: "POST",
        url: API_URL + "user/login",
        data: JSON.stringify({
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(result) {
            if (result.code == "200") {
                sessionStorage.setItem("loggedUser", JSON.stringify({
                    "loginId": JSON.parse(result.data).loginId,
                    "userId": JSON.parse(result.data).userId,
                    "email": email,
                    "loginRole": JSON.parse(result.data).loginRole,
                    "firstName": JSON.parse(result.data).firstName,
                    "authString": JSON.parse(result.data).authString,
                    "loginCount": JSON.parse(result.data).loginCount
                }));
                window.location.href = "home.html";
            } else {
                alert("Credentials are invalid");
                window.location.href = "index.html";
            }
        },
        error: function(result, status) {
            console.log(result);
        }
    });
}

//User Registration Function
function userRegistration(firstName, lastName, initials, dob, phoneNumber, gender, address, email, password) {
    $.ajax({
        type: "POST",
        url: API_URL + "user/register",
        data: JSON.stringify({
            "firstName": firstName,
            "lastName": lastName,
            "initials": initials,
            "dob": dob,
            "phoneNo": phoneNumber,
            "gender": gender,
            "address": address,
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(result) {
            console.log(result);
            if (result.code == "200") {
                alert("Registered successfully!");
                window.location.href = "index.html";
            } else {
                //window.location.href = "register.html";
            }
        },
        error: function(result, status) {
            console.log(result);
        }
    });
}

//Get User Details
function getUserDetails(id) {
    $.ajax({
        type: "GET",
        url: API_URL + "user/" + id,
        success: function(result) {
            sessionStorage.setItem("userDetails", result.data);
            const loggedUser = JSON.parse(sessionStorage.getItem("loggedUser"));
            const userDetails = JSON.parse(result.data);
            document.getElementById("fullName").innerHTML = (userDetails.firstName + " " + userDetails.lastName);
            document.getElementById("userId").innerHTML = loggedUser.userId;
            document.getElementById("dob").innerHTML = userDetails.dob;

            document.getElementById("address").innerHTML = userDetails.address;
            document.getElementById("phoneNo").innerHTML = userDetails.phoneNo;
            document.getElementById("email").innerHTML = loggedUser.email;

            if (userDetails.gender == "M") {
                document.getElementById("profileImage").src = "../images/male.png"
                document.getElementById("gender").innerHTML = "Male";
            } else {
                document.getElementById("profileImage").src = "../images/female.png"
                document.getElementById("gender").innerHTML = "Female";
            }
        },
        error: function(result, status) {
            console.log(result);
        }
    });
}

function logout() {
    sessionStorage.removeItem("loggedUser");
    sessionStorage.removeItem("userDetails");
    window.location.href = "index.html";
}