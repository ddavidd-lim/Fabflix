// let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = resultDataString;

    console.log("handle session response");
    console.log(resultDataJson);
    // let cartTableBodyElement = jQuery("#cart_body");
    //
    // for (let i = 0; i < resultArray.length; i++) {
    //     // Concatenate the html tags with resultData jsonObject
    //     let rowHTML = "";
    // }

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
    // cartTableBodyElement.append(rowHTML);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#cart_body");
    // change it to html list
    // clear the old array and show the new array in the frontend
    item_list.html("");
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        let rowHTML = "<tr>";
        rowHTML += "<th>" + resultArray[i]["movie_title"] + "</th>";
        rowHTML += "<th><button class='button removefromcart cart'>-</button>" + resultArray[i]["quantity"] + "<button class='button addtocart cart'>+</button></th>";
        rowHTML += "<th><button class='button delete'>Delete</button></th>";
        rowHTML += "<th>" + resultArray[i]["price"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["quantity"] * resultArray[i]["price"] + "</th>";
        rowHTML += "</tr>";
        item_list.append(rowHTML);
    }
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/cart", {
        method: "POST",     // POST TO UPDATE ITEMS
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

$.ajax("api/cart", {
    method: "GET",      // GET TO DISPLAY SESSION DATA: ID/LAST ACCESS
    success: handleSessionData
});

// // Bind the submit action of the form to a event handler function
// cart.submit(handleCartInfo);
