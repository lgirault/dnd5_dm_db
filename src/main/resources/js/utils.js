
var Utils = {
    GET : function (fileName, onReady){
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
        xmlhttp.open("GET",fileName,true); //true=asynchrone
        xmlhttp.send();
    },

    hide : function(toHide){
        toHide.style.display = "none";
    },

    hideEltWithId : function (toHideId) {
        var toHide = document.getElementById(toHideId);
        if(toHide != null)
            Utils.hide(toHide);
    },

    show : function(toShow){
        toShow.style.display = "block";
    },
    
    showEltWithId : function (toShowId){
        var toShow = document.getElementById(toShowId);
        if(toShow != null)
            Utils.show(toShow);
    },

    toggleBlockDisplay : function (node){
        if(node.style.display == "none")
            node.style.display = "block";
        else
            node.style.display = "none";
    },

    htmlTextToDom: function(htmlText){
        var dummy = document.createElement( 'html' );
        dummy.innerHTML = htmlText;
        return dummy;
    },

    isDefined : function (variable){
        return typeof variable !== 'undefined';
    }
}