


var UNDEFINED = "undefined";
var SCREEN = "screen";
var MONSTERS = 'monsters';
var SPELLS= 'spells';
var MONSTERS_SCREEN = "monsters_screen";
var SPELLS_SCREEN = "spells_screen";
var MENU = "menu";

function hideInnerBlockSpells(){
    var spells = document.querySelectorAll(".innerBlock"); //nodeList

    spells.forEach(function(innerBlock){
       var name = innerBlock.querySelector(".name");
       var desc = innerBlock.querySelector(".toHide");

       desc.style.display = "none";
       name.onclick = function(){
        Utils.toggleBlockDisplay(desc);
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
            b.style.width = context.columnWidth[screen_id]+"px";
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
function appendBlocAsNode(containerId, htmlText){
  var htmlDom = Utils.htmlTextToDom(htmlText);

 var links = htmlDom.querySelectorAll(".bloc a");
  links.forEach(function(l){
     asynchronizeLink(l);
  });

  var blocs = htmlDom.querySelectorAll(".bloc");

  blocs.forEach(function(b){
      var selt = document.getElementById(b.id);
      if(selt == null) 
        document.getElementById(containerId).appendChild(b);
      else
        selt.replace(b);
  });

   
}


function appendMonsterBlock(text){
    appendBlocAsNode(MONSTERS_SCREEN, text);
    hideInnerBlockSpells();
    normalizeBlockDim(MONSTERS_SCREEN);
}

function appendSpellBlock(text){
    appendBlocAsNode(SPELLS_SCREEN, text);
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

                var blocks = document.querySelectorAll("#"+context.screen+"_screen .bloc");
                blocks.forEach(function(b){
                        b.style.height = "auto";
                });
                context.setColumnWidth(context.screen+"_screen", blocWidth + (e.pageX - xInit));
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
    var thisContext = this;
    
    this.screen = MONSTERS;

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
    this.elements = {monsters : {}, spells : {}};
    
    this.appendBlock = {monsters : appendMonsterBlock, spells : appendSpellBlock};
    
    this.parseAndLoadQuery= function (query) {
        var keyVal = query.split('=');
        var key = decodeURIComponent(keyVal[0]);

        if(key == SCREEN)
            this.setScreen(keyVal[1]);
        else if(key == MENU)
            this.loadMenu(keyVal[1]);
        else if(key == "monstersColumnWidth")
            this.setColumnWidth(MONSTERS_SCREEN, keyVal[1]);
        else if(key == "spellsColumnWidth")
            this.setColumnWidth(SPELLS_SCREEN, keyVal[1]);
        else {
            var langItem= keyVal[1].split('/');
            this.loadQuery(key, langItem[0], langItem[1]);
        }
    }
    
    this.clearScreen = function(){
        var blocs = document.querySelectorAll("#"+context.screen+"_screen .bloc"); 
        blocs.forEach(function(b){ b.remove(); });
        this.elements[this.screen]= {};
        this.setURL();    
    }
    
    this.menuLang = "eng";

    this.setURL = function (){
        
        var str = "?screen="+this.screen;
        str += "&menu="+this.menuLang;
        str += "&monstersColumnWidth="+this.columnWidth[MONSTERS_SCREEN];
        str += "&spellsColumnWidth="+this.columnWidth[SPELLS_SCREEN];

        var addTypeUrl = function(type){
           thisContext.elements[type].forEach(function(id, lang){
                str += "&" + type + "=" + lang + "/" + id;
            });
        }
        addTypeUrl(MONSTERS);             
        addTypeUrl(SPELLS);             
        
        // !!! do not use  window.location.search = XXX; it reload the page !
        history.pushState({}, "", this.address + str);

    }

    this.loadQuery = function(type, lang, id){
        var relativeAddress = type + "/" + lang + "/" + id + ".html"
        
        if(!Utils.isDefined(this.elements[type][id])){
           this.elements[type][id] = "";
        }
        if(this.elements[type][id] != lang){
             this.elements[type][id] = lang;
             Utils.GET(this.address + relativeAddress, this.appendBlock[type]);
        }
    }

    
    this.loadMenu = function(lang){
        Utils.GET(lang+"/menu.html", function(text){
           var menuDom = Utils.htmlTextToDom(text); 
           var left_frame = document.getElementById("left_frame");
           
           var appendOrReplace = function(name){
               var curIndex = document.getElementById(name+"_index")
               var newIndex = menuDom.querySelectorAll("#"+name+"_index")[0];
               if(curIndex==null)
                   left_frame.appendChild(newIndex);
               else
                   curIndex.replace(newIndex);
           };
           
           appendOrReplace(SPELLS);
           appendOrReplace(MONSTERS);
           Utils.hide(otherScreen(thisContext.screen) + "_index");
           initMenuLinks();
        });
    }

    this.columnWidth = {monsters_screen : 200, spells_screen : 200};

    this.setColumnWidth = function(key, newWidth){
        this.columnWidth[key] = newWidth;
        normalizeBlockDim(key);    
    }
    

    this.setScreen = function (name){
        var other = otherScreen(name);
        this.screen = name;
        Utils.hide(other + "_screen");
        Utils.hide(other + "_index");
        Utils.show(name + "_screen");
        Utils.show(name + "_index");
    }

    this.toggleScreen = function() {
        this.setScreen(otherScreen(this.screen));
        this.setURL();
    }
}


var context = new Context();

function asynchronizeLink(link){
    link.onclick = function(e) {
       var url = e.target.href.split('?');
       context.parseAndLoadQuery(url[1]);
       context.setURL();
       return false;
    };
}

function initMenuLinks(){
    var menuLinks = document.querySelectorAll(".menuLink"); //nodeList
    menuLinks.forEach(function(menuLink){
        asynchronizeLink(menuLink);
    });

}

function initInterface(){
    var menuLang = document.querySelectorAll(".menuLang");
    menuLang.forEach(function(menuLink){
        asynchronizeLink(menuLink);
    });

    var buttons = document.querySelectorAll(".clearScreen");    

    buttons.forEach(function(b){
        b.onclick = function(e) {
            context.clearScreen();
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
    context.parseSearchString();
    context.setURL();
    initInterface();
    
   
};
