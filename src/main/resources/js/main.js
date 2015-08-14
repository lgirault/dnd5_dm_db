
NodeList.prototype.forEach = Array.prototype.forEach
HTMLCollection.prototype.forEach = Array.prototype.forEach

function loadXMLDoc(fileName, onReady)
{
    var xmlhttp;
    if (window.XMLHttpRequest){
    // code for IE7+, Firefox, Chrome, Opera, Safari
      xmlhttp=new XMLHttpRequest();
    }
    else{// code for IE6, IE5
      xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function() {
      if (xmlhttp.readyState==4 && xmlhttp.status==200){
        onReady(xmlhttp.responseText)
      }
    }
    xmlhttp.open("GET",fileName,true);
    xmlhttp.send();
}

function toggleBlockDisplay(node){
    if(node.style.display == "none")
        node.style.display = "block";
    else
        node.style.display = "none";
}

function hideInnerBlockSpells(){
    var spells = document.querySelectorAll(".innerBlock"); //nodeList

    spells.forEach(function(innerBlock){
       var name = innerBlock.querySelector(".name");
       var desc = innerBlock.querySelector(".toHide");

       console.log(name);
       console.log(desc);
       desc.style.display = "none";
       name.onclick = function(){
        toggleBlockDisplay(desc);
        return false;
       }
    });
}


function displayBlock(text){
    document.getElementById("right_frame").innerHTML = text;
    hideInnerBlockSpells();
}

function appendBlock(text){
    document.getElementById("right_frame").innerHTML += text;
    hideInnerBlockSpells();
}

function initMenuLinks(){
    var menuLinks = document.querySelectorAll(".menuLink"); //nodeList
    menuLinks.forEach(function(menuLink){
        menuLink.onclick = function(e) {
            loadXMLDoc(e.target.href, appendBlock)
            return false;
        };
    });

}

window.onload = function(){
    initMenuLinks();
    console.log()
    var address = window.location.origin + window.location.pathname;
    var query = window.location.search.substring(1);
    var keyVals = query.split('&');
    for(var i = 0; i < keyVals.length; i++){
        var keyVal = keyVals[i].split('=');
        var key = decodeURIComponent(keyVal[0]);
        var val = keyVal[1].split('.');
        var lang = decodeURIComponent(val[0]);
        var name = decodeURIComponent(val[1]);
        var fullAddress = address + key + "/" + lang + "/" + name + ".html";
        loadXMLDoc(fullAddress, displayBlock);
    }

};
