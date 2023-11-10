let star_form = $("#add-star-form");

function handleAddStarResult(starId) {
    let star_result = $("#add_star_id");
    alert("star added: " + starId)
    star_result.textContent = "StarID: " + starId;

}

function submitStarForm(formSubmitEvent) {
    console.log("submit add star form");

    $.ajax(
        "api/addStar", {
            method: "POST",
            data: star_form.serialize(),
            url: "/api/addStar",
            success: (starId) => handleAddStarResult(starId)
        }
    );
}

star_form.submit(submitStarForm);