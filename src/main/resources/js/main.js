
NodeList.prototype.forEach = Array.prototype.forEach
HTMLCollection.prototype.forEach = Array.prototype.forEach

Element.prototype.remove = function() {
        this.parentElement.removeChild(this);
}

NodeList.prototype.remove = HTMLCollection.prototype.remove = function() {
   for(var i = this.length - 1; i >= 0; i--) {
        if(this[i] && this[i].parentElement) {
            this[i].parentElement.removeChild(this[i]);
        }
   }
}

Element.prototype.prependChild = function(newChild) {
        this.insertBefore(newChild, this.firstChild)
}


Array.prototype.contains = function (elt){
    return this.indexOf(elt)!=-1;
}

var UNDEFINED = "undefined";
var SCREEN = "screen";
var MONSTERS = 'monsters';
var SPELLS= 'spells';
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
        normalizeBlockDim(MONSTERS_SCREEN);
        return false;
       }
    });
}


function normalizeBlockDim(screen_id){
    var blocks = document.querySelectorAll("#"+screen_id+" .bloc"); //nodeList
    if(blocks.length == 0)
        return;
        
    var height = blocks[0].clientHeight;
    var firstOfLine = 0;
    var setDimUntil = function(index){
        for(var i=firstOfLine; i<index; i++){
            var b = blocks[i];
            b.style.height = height+"px";
            b.style.width = context.getColumnWidth()+"px";
            var ds = b.querySelectorAll(".dragbar");
            ds.forEach(function(d){
                //d.style.height = height+"px";
                setUpGhostBarEvent(d);
            });
        }
    }
    
    var prevOffset = blocks[0].prevOffset;
    for(var i=1; i < blocks.length; i++){

        var b = blocks[i];
        var h = b.clientHeight;
        
        var newLine = b.offsetTop != prevOffset;
        if(newLine){
            setDimUntil(i);
            firstOfLine = i;
            height = 0;
        }

        if(h > height){
           height = h;
        }

        prevOffset = b.offsetTop;
    }
    setDimUntil(blocks.length);
   
    

}


function appendMonsterBlock(text){
    document.getElementById(MONSTERS_SCREEN).innerHTML += text;
    hideInnerBlockSpells();
    normalizeBlockDim(MONSTERS_SCREEN);
}

function appendSpellBlock(text){
    document.getElementById(SPELLS_SCREEN).innerHTML += text;
    normalizeBlockDim(SPELLS_SCREEN);
}


function setUpGhostBarEvent(dragbar_div_element){

    dragbar_div_element.onmousedown = function(e){

           e.preventDefault();

           var ghostbar = document.createElement("div");
           ghostbar.id = "ghostbar";

           var t = dragbar_div_element.offsetTop;
           var h = document.getElementById("right_frame").clientHeight - t ;
           ghostbar.style.height = h+"px";
           ghostbar.style.top = t+"px";
           ghostbar.style.left = (e.pageX-2) +"px";

           var blocWidth = dragbar_div_element.parentElement.clientWidth;
           var xInit = e.pageX;

           document.body.appendChild(ghostbar);

           document.onmousemove = function(e){
              ghostbar.style.left = (e.pageX-2)+"px";
           };

        document.onmouseup = function(e){
                   document.getElementById("ghostbar").remove();
                   document.onmousemove = null;
                   document.onmouseup = null;

                context.setColumnWidth(blocWidth + (e.pageX - xInit));
                var blocks = document.querySelectorAll("#"+context.screen+"_screen .bloc");
                blocks.forEach(function(b){
                        b.style.width = context.getColumnWidth()+"px";
                        b.style.height = "auto";
                });
                normalizeBlockDim(context.screen+"_screen");
        };
    };
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
    this.elements = {monsters : new Array(), spells : new Array()};
    
    this.appendBlock = {monsters : appendMonsterBlock, spells : appendSpellBlock};
    
    this.parseAndLoadQuery= function (query) {
        var keyVal = query.split('=');
        var key = decodeURIComponent(keyVal[0]);

        if(key == SCREEN){
            this.setScreen(keyVal[1]);
        }
        else {
            this.loadQuery(key, keyVal[1]);
        }
    }
    var thisContext = this;
    this.setURL = function (){
        
        var str = "?screen="+this.screen;
        
        var addTypeUrl = function(type){
            for(var i =0; i< thisContext.elements[type].length; i++){
                str += "&" + type + "=" + thisContext.elements[type][i];
            }
        }
        addTypeUrl(MONSTERS);             
        addTypeUrl(SPELLS);             
        
        // !!! do not use  window.location.search = XXX; it reload the page !
         history.pushState({}, "", this.address + str);

    }

    this.loadQuery = function(type, query){
        var relativeAddress = type + "/" + query + ".html"
        
        if(!this.elements[type].contains(query)){
            this.elements[type].push(query);
            loadXMLDoc(this.address + relativeAddress, this.appendBlock[type]);
        }
    }

    this.columnWidth = {monsters : 200, spells : 200};
    
    this.getColumnWidth = function(){
        return this.columnWidth[this.screen];
    }

    this.setColumnWidth = function(newWidth){
        this.columnWidth[this.screen] = newWidth;    
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
        context.toggleScreen();
        return false;
    };
}

window.onload = function(){
    initMenuLinks();
    context.parseSearchString();
    if(context.screen == UNDEFINED)
        context.setScreen(MONSTERS);

    var buttons = document.querySelectorAll(".clearScreen");    

    buttons.forEach(function(b){
        b.onclick = function(e){
            console.log("TODO !");
            return false;
        };   
    });
};
