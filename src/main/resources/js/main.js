

function loadXMLDoc(fileName, target)
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
        document.getElementById(target).innerHTML=xmlhttp.responseText;
      }
    }
    xmlhttp.open("GET",fileName,true);
    xmlhttp.send();
}


window.onclick = function(e) {
    console.log(e.target);
    console.log(e.target.href);
    loadXMLDoc(e.target.href, "right_frame")
    return false;
};