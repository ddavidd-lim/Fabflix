/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate single movie h1
    let movieTitleElement = jQuery("#single_movie_title");
    movieTitleElement.append("<p><em>" + resultData[0]["movie_title"] +
        "</em> (" + resultData[0]["movie_year"] + ")</p>");

    // populate the movie info h3
    // find the empty h3 body by id "single_movie_info"
    let movieInfoElement = jQuery("#single_movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append(
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>" +
        // CREATE ADDTOCART CLASS for button
        "<button class='addtocart button' data-movie-id=" + resultData[0]["movie_id"] + ">Add</button>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the genre/star table
    // Find the empty table body by id "star_movie_table_body"
    let starTableBodyElement = jQuery("#star_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" +
            '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
            + resultData[i]["star_name"] +
            '</a>' +
            "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }

    // Now do the same for genres
    let rowHTML = "<tr>";
    rowHTML += "<th>";
    let genreTableBodyElement = jQuery("#genre_movie_table_body");
    let genres = resultData[0]["genre"];
    let genresArray = genres.split(",");
    for (let i = 0; i < genresArray.length; i++) {
        rowHTML += "<a href='results.html?type=browse&genre=" + genresArray[i].trim() + "'>";
        rowHTML += genresArray[i];
        rowHTML += "</a>";
        if (i < genresArray.length - 1) {
            rowHTML += ", ";
        }
        // Append the row created to the table body, which will refresh the page

    }
    rowHTML += "</th></tr>";
    // alert(rowHTML);
    genreTableBodyElement.append(rowHTML);

    // Attach a click event listener to each "addtocart" button
    const addToCartButtons = document.querySelectorAll('.addtocart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const movieId = button.getAttribute('data-movie-id');
            addToSessionCart(movieId);
        });
    });

}

/**
 * Submit form content with POST method
 * @param movieId
 */
function addToSessionCart(movieId) {
    // Send an AJAX request to the server to add the movie to the session cart
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/cart", // Adjust the URL as needed
        data: { movie_id: movieId, action: "add"},
        success: () => {
            alert("Successfully added to cart");
        },
        error: (jqXHR, textStatus) => {
            alert("Error: " + textStatus);
        }

    });
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});