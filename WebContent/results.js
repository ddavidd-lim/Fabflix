/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

let resultLength = 0;
function getParameterByName(target) {
    let url = window.location.href;
    //
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));

}

function handleResults(resultData) {
    // console.log("handleStarResult: populating results from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_result_table_body");
    let limit_num = parseInt(getParameterByName("limit"));
    resultLength = resultData.length;

    // Iterate through resultData, no more than 10 entries
    let next_actor = 0;
    let next_genre = 0;
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        // find way to insert all genres
        let genresString = resultData[i]["genres"];
        let genresArray = genresString.split(',');

        rowHTML += "<th>";
        for (let j = 0; j < Math.min(3, genresArray.length); j++) {
            rowHTML += "<a href='results.html?type=browse&genre=" + genresArray[j].trim() + "'>"
            rowHTML += genresArray[j];
            rowHTML += "</a>"
            if (j < genresArray.length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</th>"

        // for actors
        // SAMPLE SOLUTION
        let starsString = resultData[i]['top3Stars'];
        let starArray = starsString.split(', ');
        let starIdArray = [];
        let starNameArray = [];
        for (let string of starArray){
            let idName = string.split(':'); // [0] = id, [1] = name
            starIdArray.push(idName[0]);
            starNameArray.push(idName[1]);
        }
        rowHTML += "<th>" +
            '<a href="single-star.html?id=' + starIdArray[0] + '">'
            + starNameArray[0] +
            '</a>, ' +
            '<a href="single-star.html?id=' + starIdArray[1] + '">'
            + starNameArray[1] +
            '</a>, ' +
            '<a href="single-star.html?id=' + starIdArray[2] + '">'
            + starNameArray[2] +
            '</a> ' +
            "</th>"


        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th><button class='addtocart button' data-movie-id=" + resultData[i]["movie_id"] + ">Add</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    // Attach a click event listener to each "addtocart" button
    const addToCartButtons = document.querySelectorAll('.addtocart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const movieId = button.getAttribute('data-movie-id');
            addToSessionCart(movieId);
        });
    });
  changeNavButtons(resultData.length)
}


function addToSessionCart(movieId) {
    // Send an AJAX request to the server to add the movie to the session cart
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/cart", // Adjust the URL as needed
        data: { movie_id: movieId, action: "add" },
        success: () => {
            alert("Successfully added to cart");
        },
        error: (jqXHR, textStatus,) => {
            alert("Error: " + textStatus);
        }

    });    
}


function changeNavButtons(table_rows) {
    let url = window.location.toString();
    if (!url.includes("&page=")) // we assume page 1
    {
        document.getElementById("prevButton").disabled = true;
        document.getElementById("nextButton").disabled = false;
        document.getElementById('nextButtonLink').setAttribute('href',url + "&page=2");
    }
    else {
        let pageNum = parseInt(url.split("&page=")[1]);
        url = url.split("&page=")[0];

        if (pageNum === 1) {
            document.getElementById("prevButton").disabled = true;
        }
        else
        {
            document.getElementById("prevButton").disabled = false;
            document.getElementById("prevButtonLink").setAttribute('href', url + "&page=" + (pageNum-1).toString());
        }
        let limitNum = parseInt(url.split("&limit=")[1]);
        if (table_rows < limitNum)
        {
            document.getElementById("nextButton").disabled = true;
        }
        else
        {
            document.getElementById("nextButtonLink").setAttribute('href', url + "&page=" + (pageNum+1).toString());
        }
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let title = getParameterByName("movietitle");
let year = getParameterByName("movieyear");
let director = getParameterByName("director");
let star = getParameterByName("moviestar");
let genre = getParameterByName("genre");
let type = getParameterByName("type");
let sort = getParameterByName("sort");
let limit = getParameterByName("limit");
let page = getParameterByName("page");

jQuery(".last-search").attr("href", window.location.href);

const search_result = window.location.href;
sessionStorage.setItem('search_result', search_result);

jQuery("#typeForm_results").val(type);
jQuery("#movietitleForm_results").val(title);
jQuery("#movieyearForm_results").val(year);
jQuery("#directorForm_results").val(director);
jQuery("#starForm_results").val(star);
jQuery("#genreForm_results").val(genre);

// Makes the HTTP GET request and registers on success callback function
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/results?type=" + type + "&movietitle=" + title + "&movieyear=" + year + "&director=" + director + "&moviestar=" +
        star + "&genre=" + genre + "&sort=" + sort + "&limit=" + limit + "&page=" + page, // Setting request url
    success: (resultData) => handleResults(resultData)
});
