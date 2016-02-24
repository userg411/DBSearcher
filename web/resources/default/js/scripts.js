function calculatePiListener(data) {
    var statusElement = document.getElementById("status");
    var img = document.getElementById("ajaxloader");
    if (data.status === "begin")
        statusElement.innerHTML = "Sent request. Waiting for response...";
    else if (data.status === "complete")
        statusElement.innerHTML = "Response received";
    else if (data.status === "success")
        statusElement.innerHTML = "Rendered successfully";
}
 function calculatePiListener2(data) {
    
    var img = document.getElementById("ajaxloader");
    if (data.status === "begin")
        img.style.display = 'block';
    else if (data.status === "complete")
        img.style.display = 'none';
    
}


