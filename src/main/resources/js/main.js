
NodeList.prototype.forEach = Array.prototype.forEach
HTMLCollection.prototype.forEach = Array.prototype.forEach

Array.prototype.contains = function (elt){
    return this.indexOf(elt)!=-1;
}

var UNDEFINED = "undefined";
var SCREEN = "screen";
var MONSTERS = "monsters";
var SPELLS= "spells";
var MONSTERS_SCREEN = "monsters_screen";
var SPELLS_SCREEN = "spells_screen";

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

       desc.style.display = "none";
       name.onclick = function(){
        toggleBlockDisplay(desc);
        normalizeBlockHeight(MONSTERS_SCREEN);
        return false;
       }
    });
}


function normalizeBlockHeight(id){
    var height = 0;
    var blocks = document.querySelectorAll("#"+id+" .bloc"); //nodeList

    blocks.forEach(function(b){
           var h = b.clientHeight;
           if(h > height){
             height = h;
           }
    });

    blocks.forEach(function(b){
        b.clientHeight = height;
    });
}

function appendMonsterBlock(text){
    document.getElementById(MONSTERS_SCREEN).innerHTML += text;
    hideInnerBlockSpells();
    normalizeBlockHeight(MONSTERS_SCREEN);
}

function appendSpellBlock(text){
    document.getElementById(SPELLS_SCREEN).innerHTML += text;
    normalizeBlockHeight(SPELLS_SCREEN);
}






function otherScreen(name){
    if(name == SPELLS)
        return MONSTERS;
    else if( name == MONSTERS)
        return SPELLS;
    else
        return UNDEFINED;
}

function LangEntry(lang, name){
    this.lang = lang;
    this.name = name;
}

function Context(){

    this.screen = UNDEFINED;

    this.address = window.location.origin + window.location.pathname;

    this.parseSearchString = function (){
        var searchString = window.location.search;
            var query = searchString.substring(1);
            var keyVals = query.split('&');
            for(var i = 0; i < keyVals.length; i++){
                if(keyVals[i].length > 0)
                   this.parseAndLoadQuery(keyVals[i]);
            }

    }

    this.monsters = new Array();
    this.spells = new Array();

    this.parseAndLoadQuery= function (query) {
        var keyVal = query.split('=');
        var key = decodeURIComponent(keyVal[0]);

        if(key == SCREEN){
            this.setScreen(keyVal[1]);
        }
        else {
            this.loadQuery(key, keyVal[1]);
        }
        //var relativeAddress = parseDBEntry(keyVal[1]);
    }

    this.setURL = function (){
        var str = "?screen="+this.screen;
        for(var i =0; i< this.monsters.length; i++){
           str += "&" + MONSTERS + "=" + this.monsters[i];
        }
        for(var i =0; i< this.spells.length; i++){
           str += "&" + SPELLS + "=" + this.spells[i];
        }


        // !!! do not use  window.location.search = XXX; it reload the page !

         history.pushState({}, "", this.address + str);

    }

    this.loadQuery = function(type, query){
        var answerProcess;
        var relativeAddress = type + "/" + query + ".html"
        var load = false;

       if (type == MONSTERS){
            if(!this.monsters.contains(query)){
                this.monsters.push(query);
                answerProcess = appendMonsterBlock;
                load = true;
            }
       }
       else if (type == SPELLS){
            if(!this.spells.contains(query)){
                this.spells.push(query);
                answerProcess = appendSpellBlock;
                load = true;
            }
       }

       if(load){
            loadXMLDoc(this.address + relativeAddress, answerProcess);
       }

    }

    this.setScreen = function (name){
        var other = otherScreen(name);
        this.screen = name;
        hide(other + "_screen");
        hide(other + "_index");
        show(name + "_screen");
        show(name + "_index");
    }

    this.toggleScreen = function() {
        this.setScreen(otherScreen(this.screen));
        this.setURL();
    }
}

function hide(toHideId) {
    document.getElementById(toHideId).style.display = "none";
}
function show(toShowId){
    document.getElementById(toShowId).style.display = "block";
}


var context = new Context();

function initMenuLinks(){
    var menuLinks = document.querySelectorAll(".menuLink"); //nodeList
    menuLinks.forEach(function(menuLink){
        menuLink.onclick = function(e) {
            var url = e.target.href.split('?');
            context.parseAndLoadQuery(url[1]);
            context.setURL();
            return false;
        };
    });

    var toggleButton = document.getElementById("toggle_button");

    toggleButton.onclick = function(e) {
        console.log("click");
        context.toggleScreen();
        return false;
    };
}

window.onload = function(){
    initMenuLinks();
    context.parseSearchString();
    if(context.screen == UNDEFINED)
        context.setScreen(MONSTERS);
};
