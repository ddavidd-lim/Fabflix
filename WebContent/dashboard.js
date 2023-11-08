function getMetadata(resultData)
{
    let data = jQuery("#metadata");
    let sql_tables = resultData;

    for(let i=0; i < sql_tables.length; i++)
    {
        // make the table with title
        let title = document.createElement("h2");
        title.innerHTML = sql_tables[i]["table_name"];
        data.append(title);

        let table = document.createElement("table");
        // make table head
        let head =  document.createElement("thead");
        let tr = document.createElement("tr");
        let name_col = tr.insertCell(-1);
        let type_col = tr.insertCell(-1);

        name_col.textContent = "Attribute";
        type_col.textContent = "Type";
        head.append(tr);
        table.appendChild(head);


        table.className = "table table-striped";
        let tbody = document.createElement("tbody");
        table.appendChild(tbody);
        for (let j = 0; j < sql_tables[i]['content'].length; j++)
        {
            let row = document.createElement("tr");
            name_col = row.insertCell(-1);
            type_col = row.insertCell(-1);

            name_col.textContent = sql_tables[i]['content'][j]['name'];
            type_col.textContent = sql_tables[i]['content'][j]['type'];
            tbody.appendChild(row);
        }
        data.append(table);
        data.append("<br>");
    }
}


jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/dashboard",
    success: (resultData) => getMetadata(resultData)
});