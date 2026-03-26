let loader = document.getElementById("loader");
window.addEventListener("load", function () {
    loader.style.display = "none";
})
console.log("Welcome To Swift Builder");
if (window.screen.width > 630) {
    //Add your javascript for large screens here 
    let startBtn = document.getElementById("startBtn");
    startBtn.addEventListener("click", function () {
        console.log("You Just Clicked On Get Started Button");
        let form = document.getElementById("box");
        form.style.top = "410px";
    })
    let createProfile = document.getElementById("createProfile");
    createProfile.addEventListener("click", function () {
        console.log("You Just Clicked On Get Started Button");
        let form = document.getElementById("box");
        form.style.top = "410px";
    })

    let nextBtn1 = document.getElementById("nextBtn1");
    nextBtn1.addEventListener("click", function () {
        console.log("Now Fill Eduction Form");
        let form1 = document.getElementById("form1");
        form1.style.transform = "translateY(-600px)";
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-530px)";
    });

    let backBtn1 = document.getElementById("backBtn1");
    backBtn1.addEventListener("click", function () {
        console.log("Backed Form");
        let form1 = document.getElementById("form1");
        form1.style.transform = "translateY(0px)";
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(0px)";
    })
    let nextBtn2 = document.getElementById("nextBtn2");
    nextBtn2.addEventListener("click", function () {
        console.log("Now Fill Skills Form");
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-1200px)";
        let form3 = document.getElementById("form3");
        form3.style.transform = "translateY(-1025px)";
    });
    let backBtn2 = document.getElementById("backBtn2");
    backBtn2.addEventListener("click", function () {
        console.log("Backed Form");
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-520px)";
        let form3 = document.getElementById("form3");
        form3.style.transform = "translateY(-100px)";
    })
    let nextBtn3 = document.getElementById("nextBtn3");
    nextBtn3.addEventListener("click", function () {
        console.log("Now Fill Strenght & Hobbies Form");
        let form2 = document.getElementById("form3");
        form2.style.transform = "translateY(-1700px)";
        let form3 = document.getElementById("form4");
        form3.style.transform = "translateY(-1580px)";
    });
    let backBtn3 = document.getElementById("backBtn3");
    backBtn3.addEventListener("click", function () {
        console.log("Backed Form");
        let form2 = document.getElementById("form3");
        form2.style.transform = "translateY(-1020px)";
        let form3 = document.getElementById("form4");
        form3.style.transform = "translateY(-100px)";
    })
}
else if (window.screen.width >= 375) {
    //   document.body.style.background = "#ff0099";
    let startBtn = document.getElementById("startBtn");
    startBtn.addEventListener("click", function () {
        console.log("You Just Clicked On Get Started Button");
        let form = document.getElementById("box");
        form.style.top = "700px";
    })
    let createProfile = document.getElementById("createProfile");
    createProfile.addEventListener("click", function () {
        console.log("You Just Clicked On Get Started Button");
        let form = document.getElementById("box");
        form.style.top = "700px";
    })

    let nextBtn1 = document.getElementById("nextBtn1");
    nextBtn1.addEventListener("click", function () {
        console.log("Now Fill Eduction Form");
        let form1 = document.getElementById("form1");
        form1.style.transform = "translateY(-700px)";
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-580px)";
    });

    let backBtn1 = document.getElementById("backBtn1");
    backBtn1.addEventListener("click", function () {
        console.log("Backed Form");
        let form1 = document.getElementById("form1");
        form1.style.transform = "translateY(0px)";
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(0px)";
    })
    let nextBtn2 = document.getElementById("nextBtn2");
    nextBtn2.addEventListener("click", function () {
        console.log("Now Fill Skills Form");
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-1300px)";
        let form3 = document.getElementById("form3");
        form3.style.transform = "translateY(-1155px)";
    });
    let backBtn2 = document.getElementById("backBtn2");
    backBtn2.addEventListener("click", function () {
        console.log("Backed Form");
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-520px)";
        let form3 = document.getElementById("form3");
        form3.style.transform = "translateY(-100px)";
    })
    let nextBtn3 = document.getElementById("nextBtn3");
    nextBtn3.addEventListener("click", function () {
        console.log("Now Fill Strenght & Hobbies Form");
        let form2 = document.getElementById("form3");
        form2.style.transform = "translateY(-1900px)";
        let form3 = document.getElementById("form4");
        form3.style.transform = "translateY(-1800px)";
    });
    let backBtn3 = document.getElementById("backBtn3");
    backBtn3.addEventListener("click", function () {
        console.log("Backed Form");
        let form2 = document.getElementById("form3");
        form2.style.transform = "translateY(-1020px)";
        let form3 = document.getElementById("form4");
        form3.style.transform = "translateY(-100px)";
    })
}
else {
    //Add your javascript for small screens here 
    // document.body.style.background = "#181818";
    let startBtn = document.getElementById("startBtn");
    startBtn.addEventListener("click", function () {
        console.log("You Just Clicked On Get Started Button");
        let form = document.getElementById("box");
        form.style.top = "350px";
    })
    let createProfile = document.getElementById("createProfile");
    createProfile.addEventListener("click", function () {
        console.log("You Just Clicked On Get Started Button");
        let form = document.getElementById("box");
        form.style.top = "350px";
    })

    let nextBtn1 = document.getElementById("nextBtn1");
    nextBtn1.addEventListener("click", function () {
        console.log("Now Fill Eduction Form");
        let form1 = document.getElementById("form1");
        form1.style.transform = "translateY(-700px)";
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-580px)";
    });

    let backBtn1 = document.getElementById("backBtn1");
    backBtn1.addEventListener("click", function () {
        console.log("Backed Form");
        let form1 = document.getElementById("form1");
        form1.style.transform = "translateY(0px)";
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(0px)";
    })
    let nextBtn2 = document.getElementById("nextBtn2");
    nextBtn2.addEventListener("click", function () {
        console.log("Now Fill Skills Form");
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-1300px)";
        let form3 = document.getElementById("form3");
        form3.style.transform = "translateY(-1155px)";
    });
    let backBtn2 = document.getElementById("backBtn2");
    backBtn2.addEventListener("click", function () {
        console.log("Backed Form");
        let form2 = document.getElementById("form2");
        form2.style.transform = "translateY(-520px)";
        let form3 = document.getElementById("form3");
        form3.style.transform = "translateY(-100px)";
    })
    let nextBtn3 = document.getElementById("nextBtn3");
    nextBtn3.addEventListener("click", function () {
        console.log("Now Fill Strenght & Hobbies Form");
        let form2 = document.getElementById("form3");
        form2.style.transform = "translateY(-1800px)";
        let form3 = document.getElementById("form4");
        form3.style.transform = "translateY(-1750px)";
    });
    let backBtn3 = document.getElementById("backBtn3");
    backBtn3.addEventListener("click", function () {
        console.log("Backed Form");
        let form2 = document.getElementById("form3");
        form2.style.transform = "translateY(-1020px)";
        let form3 = document.getElementById("form4");
        form3.style.transform = "translateY(-100px)";
    })
}


// Download CV Btn
let cvBtn = document.getElementById("download");
cvBtn.addEventListener("click", function () {
    let myname = document.getElementById("myname").value;
    localStorage.setItem("myname", myname);
    myname.value = "";
    let email = document.getElementById("email").value;
    localStorage.setItem("email", email);
    email.value = "";
    let tel = document.getElementById("tel").value;
    localStorage.setItem("tel", tel);
    tel.value = "";
    let profileText = document.getElementById("profileText").value;
    localStorage.setItem("profileText", profileText);
    profileText.value = "";
    let degree = document.getElementById("degree").value;
    localStorage.setItem("degree", degree);
    degree.value = "";
    let clgname = document.getElementById("clgname").value;
    localStorage.setItem("clgname", clgname);
    clgname.value = "";
    let hsc = document.getElementById("hsc").value;
    localStorage.setItem("hsc", hsc);
    hsc.value = "";
    let sclname = document.getElementById("sclname").value;
    localStorage.setItem("sclname", sclname);
    sclname.value = "";
    let skills = document.getElementById("skills").value;
    localStorage.setItem("skills", skills);
    skills.value = "";
    let projectTitle = document.getElementById("projectTitle").value;
    localStorage.setItem("projectTitle", projectTitle);
    projectTitle.value = "";
    let projectInfo = document.getElementById("projectInfo").value;
    localStorage.setItem("projectInfo", projectInfo);
    projectInfo.value = "";
    let projectURL = document.getElementById("projectURL").value;
    localStorage.setItem("projectURL", projectURL);
    projectURL.value = "";
    let li1 = document.getElementById("li1").value;
    localStorage.setItem("li1", li1);
    li1.value = "";
    let li2 = document.getElementById("li2").value;
    localStorage.setItem("li2", li2);
    li2.value = "";
    let li3 = document.getElementById("li3").value;
    localStorage.setItem("li3", li3);
    li3.value = "";
    let h1 = document.getElementById("h1").value;
    localStorage.setItem("h1", h1);
    h1.value = "";
    let h2 = document.getElementById("h2").value;
    localStorage.setItem("h2", h2);
    h2.value = "";
    return false;
})