let star_form = $("#add-star-form");
let star_result = $("#add_star_id");

function handleAddStarResult(resultDataString) {
    // let result = JSON.parse(resultDataString);
    console.log("handle results");
    console.log(resultDataString["message"]);
    star_result.text("StarID: " + resultDataString["message"]);

}

function submitStarForm(formSubmitEvent) {
    console.log("submit add star form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addStar", {
            method: "POST",
            data: star_form.serialize(),
            success: handleAddStarResult,
            error: function(error) {
                console.error("Error:", error["errorMessage"]);
            }
        }
    );
}

star_form.submit(submitStarForm);