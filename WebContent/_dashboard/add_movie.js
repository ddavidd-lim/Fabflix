let movie_form = $("#add-movie-form");
let movie_result = $("#add_movie_result");

function handleAddMovieResult(resultDataString) {
    console.log("handle results");
    console.log(resultDataString["message"]);
    movie_result.append("<p>" + resultDataString["message"] + "</p>");
}
function submitMovieForm(event) {
    console.log("submit add movie form");

    event.preventDefault();

    $.ajax(
        "api/addMovie", {
            method: "POST",
            data: movie_form.serialize(),
            success: handleAddMovieResult,
            error: function(error) {
                console.error("Error:", error["errorMessage"]);
            }
        }
    );
}

movie_form.submit(submitMovieForm);
