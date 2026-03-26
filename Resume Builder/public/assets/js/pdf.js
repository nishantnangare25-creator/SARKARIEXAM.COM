let loader = document.getElementById("loader");
window.addEventListener("load", function () {
    loader.style.display = "none";
})
window.onload = function () {
    document.getElementById("download")
        .addEventListener("click", () => {
            const invoice = this.document.getElementById("invoice");
            console.log(invoice);
            console.log(window);
            
            var opt = {
                margin: 0.1,
                filename: `MyResume-SwfitBuilder.pdf`,
                image: { type: 'jpeg', quality: 100 },
                html2canvas: { scale: 1 },
                jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
            };
            html2pdf().from(invoice).set(opt).save();
        })
}


// Print Name

document.getElementById("myname").innerHTML = localStorage.getItem("myname");
document.getElementById("email").innerHTML = localStorage.getItem("email");
document.getElementById("tel").innerHTML = localStorage.getItem("tel");
document.getElementById("profileText").innerHTML = localStorage.getItem("profileText");
document.getElementById("degree").innerHTML = localStorage.getItem("degree");
document.getElementById("clgname").innerHTML = localStorage.getItem("clgname");
document.getElementById("hsc").innerHTML = localStorage.getItem("hsc");
document.getElementById("sclname").innerHTML = localStorage.getItem("sclname");
document.getElementById("skills").innerHTML = localStorage.getItem("skills");
document.getElementById("projectInfo").innerHTML = localStorage.getItem("projectInfo");
document.getElementById("projectURL").href = localStorage.getItem("projectURL");
document.getElementById("li1").innerHTML = localStorage.getItem("li1");
document.getElementById("li2").innerHTML = localStorage.getItem("li2");
document.getElementById("li3").innerHTML = localStorage.getItem("li3");
document.getElementById("h1").innerHTML = localStorage.getItem("h1");
document.getElementById("h2").innerHTML = localStorage.getItem("h2");

let imgBox = document.getElementById("imgBox");

let loadFile = function (event) {
    imgBox.style.backgroundImage = "url(" + URL.createObjectURL(event.target.files[0]) + ")";
}
if(window.screen.width > 630){
    setAtt
}
else if(window.screen.width < 630){
    document.body.style.background = "#fff";
}
else{
    document.body.style.background = "#fff";
}