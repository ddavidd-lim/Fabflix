let card_info = $("#card_info");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function getTotal(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);

    // show cart information
    handleTotalDisplay(resultDataJson["previousItems"]);
    }

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleTotalDisplay(resultArray) {
    console.log(resultArray);
    let subtotal = $(".subtotal");
    let res = "Subtotal: $";
    let total = 0;
    for (let i = 0; i < resultArray.length; i++) {
        total += parseInt(resultArray[i]["price"]);
    }
    res += total;

    // clear the old array and show the new array in the frontend
    subtotal.html("");
    subtotal.append(res);
}


/**
 * Submit form content with POST method
 * @param cardEvent
 */
function handleCardInfo(cardEvent) {
    console.log("submit card form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cardEvent.preventDefault();

    $.ajax("api/cart", {
        method: "POST",     // POST TO UPDATE ITEMS
        data: card_info.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
            alert("Successfully added to cart");
        }
    });

    // clear input form
    card_info[0].reset();
}

$.ajax("api/cart", {
    method: "GET",      // GET TO DISPLAY SESSION DATA: ID/LAST ACCESS
    success: getTotal
});


// // Bind the submit action of the form to a event handler function
card_info.submit(handleCardInfo);
