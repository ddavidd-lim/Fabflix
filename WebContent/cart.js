// let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let cart_list = $("#cart_body");
    // change it to html list
    let rowHTML = "";
    cart_list.empty()
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a row
        rowHTML += "<tr>";
        rowHTML += "<td>" + resultArray[i]["movie_title"] + "</td>";
        rowHTML += "<td><button class='button removefromcart cart' " +
            "data-movie-id=" + resultArray[i]["movie_id"] + ">-</button>" + resultArray[i]["quantity"] + "<button class='button addtocart cart' " +
            "data-movie-id=" + resultArray[i]["movie_id"] + ">+</button></td>";
        rowHTML += "<td><button class='button delete' " +
            "data-movie-id=" + resultArray[i]["movie_id"] + ">Delete</button></td>";
        rowHTML += "<td>" + resultArray[i]["price"] + "</td>";
        rowHTML += "<td>" + resultArray[i]["quantity"] * resultArray[i]["price"] + "</td>";
        rowHTML += "</tr>";
    }
    // clear the old array and show the new array in the frontend

    cart_list.append(rowHTML);

    const addToCartButtons = document.querySelectorAll('.addtocart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const movieId = button.getAttribute('data-movie-id');
            addToSessionCart(movieId);
        });
    });
    const removeButtons = document.querySelectorAll('.removefromcart');
    removeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const movieId = button.getAttribute('data-movie-id');
            removeFromCart(movieId);
        });
    });
    const deleteButtons = document.querySelectorAll('.delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const movieId = button.getAttribute('data-movie-id');
            deleteFromCart(movieId);
        });
    });
}

function addToSessionCart(movieId) {
    // Send an AJAX request to the server to add the movie to the session cart
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/cart", // Adjust the URL as needed
        data: { movie_id: movieId, action: "add" },
        success: resultDataString => {
            // let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataString["previousItems"])
            alert("Successfully added to cart");
        },
        error: (jqXHR, textStatus) => {
            alert("AddError: " + textStatus);
        }

    });
}

function removeFromCart(movieId) {
    // Send an AJAX request to the server to add the movie to the session cart
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/cart", // Adjust the URL as needed
        data: { movie_id: movieId, action: "remove" },
        success: resultDataString => {
            handleCartArray(resultDataString["previousItems"]);
            alert("Successfully removed from cart");
        },
        error: (jqXHR, textStatus) => {
            alert("RemoveError: " + textStatus);
        }

    });
}

function deleteFromCart(movieId) {
    // Send an AJAX request to the server to add the movie to the session cart
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/cart", // Adjust the URL as needed
        data: { movie_id: movieId, action: "delete" },
        success: resultDataString => {
            handleCartArray(resultDataString["previousItems"]);
            alert("Successfully deleted from cart");
        },
        error: (jqXHR, textStatus) => {
            alert("DeleteError: " + textStatus);
        }

    });
}

$.ajax("api/cart", {
    method: "GET",      // GET TO DISPLAY SESSION DATA: ID/LAST ACCESS
    success: handleSessionData
});

// // Bind the submit action of the form to a event handler function
// cart.submit(handleCartInfo);
