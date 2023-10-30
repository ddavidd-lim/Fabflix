/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleGenreResult(resultData) {
    console.log("handleStarResult: populating genres list from resultData");

    let mainPageGenresBodyElement = jQuery("#main_page_genres");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<li class='main-genres-li'>";
        rowHTML += "<a href=results.html?type=browse&genre=" + resultData[i]['genreName'] +
            " class='genre-link' onclick=" + "changeGenre(" + resultData[i]['genreName'] + ")>";
        rowHTML += resultData[i]['genreName'];
        rowHTML += "</a>"
        rowHTML += "</li>";

        // Append the row created to the table body, which will refresh the page
        mainPageGenresBodyElement.append(rowHTML);
    }
}

function handleMovieTitleResult(resultData) {
    console.log("handleStarResult: populating movie letters from resultData");

    let mainPageMovieTitleBodyElement = jQuery("#numeric_alpha_titles");
    let result = 0;
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        if (/^[a-zA-Z0-9]+$/.test(resultData[i]["movieLetter"]))
        {
            let rowHTML = "";
            rowHTML += "<li class='main-genres-li'>";
            rowHTML += "<a href=results.html?type=browse&movietitle=" + resultData[i]['movieLetter'] +
                " class='letter-link' onclick=" + "changeTitle(" + resultData[i]['movieLetter'] + ")>";
            rowHTML += resultData[i]["movieLetter"];
            rowHTML += "</a>";
            rowHTML += "</li>";
            // Append the row created to the table body, which will refresh the page
            mainPageMovieTitleBodyElement.append(rowHTML);
        }
    }

    let row = "<li class='main-genres-li'>";
    row += "<a href='results.html?type=browse&movietitle=*' class='letter-link' onclick=" +
        "changeTitle(" + "'*'" + ")>";
    row += "*";
    row += "</a>"
    row += "</li>";
    mainPageMovieTitleBodyElement.append(row);

    console.log("movieTitles added");
}

function changeGenre(genre) {
    let genreForm = jQuery("#genreForm");
    let typeForm = jQuery("#typeForm");
    genreForm.value = genre;
    typeForm.value = "browse";
}

function changeTitle(letter) {
    let typeForm = jQuery("#typeForm");
    let titleForm = jQuery("#movietitleForm");
    titleForm.value = letter;
    typeForm.value = "browse";
}

// Makes the HTTP GET request and registers on success callback function

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genres", // Setting request url, which is mapped by GenreServlet in main_page.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movieTitle",
    success: (resultData) => handleMovieTitleResult(resultData)
});

