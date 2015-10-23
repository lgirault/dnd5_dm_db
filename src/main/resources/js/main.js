

var SPELLS = "spells"
var MONSTERS = "monsters"

function hideInnerBlockSpells(screen){
    var spells = screen.root.querySelectorAll(".innerBlock"); //nodeList

    spells.forEach(function(innerBlock){
       var name = innerBlock.querySelector(".name");
       var desc = innerBlock.querySelector(".toHide");

       desc.style.display = "none";
       name.onclick = function(){
        Utils.toggleBlockDisplay(desc);
        screen.normalizeBlockDim();
        return false;
       }
    });
}


function appendBlocAsNode(container, htmlText){
  var htmlDom = Utils.htmlTextToDom(htmlText);

  var links = htmlDom.querySelectorAll(".bloc a");
      links.forEach(function(l){
         asynchronizeLink(l, context.parseAndLoadQuery);
      });

  var blocs = htmlDom.querySelectorAll(".bloc");

  blocs.forEach(function(b){
     var selt = document.getElementById(b.id);
     if(selt == null) 
        container.appendChild(b);
     else
        selt.replace(b);
  });
}

function appendMonsterBlock(screen, text){
    appendBlocAsNode(screen.root, text);
    hideInnerBlockSpells(screen);
    screen.normalizeBlockDim();
}

function appendSpellBlock(screen, text){
    appendBlocAsNode(screen.root, text);
    screen.normalizeBlockDim();
}


function Bloc(type, lang, id){
    var thisBloc = this;
    this.id = id;
    this.lang = lang;
    this.type = type;

    this.relativeAddress = function(){
       return "/" + this.type + "/" + this.lang + "/" + this.id + ".html"   
    }

    this.append = {monsters : appendMonsterBlock, spells : appendSpellBlock}[type];

    this.load = function(screen){
        Utils.GET(thisBloc.relativeAddress(), thisBloc.append.bind(null, screen));
        return false;
    }
}

// var MonsterBloc = Bloc.bind(null, MONSTERS)
// var SpellBloc = Bloc.bind(null, SPELLS)


function Screen(screen_id){
    var thisScreen = this;
    this.id = screen_id;
    this.columnWidth = 200;
    
    this.setColumnWidth = function(newWidth){
        thisScreen.columnWidth = newWidth;
        thisScreen.normalizeBlockDim();    
    }
    

    this.root = null;
    
    this.create = function(container){
        thisScreen.root = document.createElement("div");
        
        var title = document.createElement("h1");
        title.appendChild(document.createTextNode(thisScreen.id));
        thisScreen.root.appendChild(title);
        container.appendChild(thisScreen.root);
        
        thisScreen.elements.forEach(function(k,b){
            b.load(thisScreen);
        });
    }
    
    this.clear = function(){
        var blocs = thisScreen.root.querySelectorAll(".bloc");
        blocs.forEach(function(b){ b.remove(); });
        thisScreen.elements = {};
    }

    this.elements = {};
    
    this.loadQuery = function(type, lang, id){
        
        var b = thisScreen.elements[id];
        if(Utils.isDefined(b) && b.lang != lang){
           b.lang = lang;
           b.load(thisScreen);
        }
           
        if(!Utils.isDefined(b)) {
           b = new Bloc(type, lang, id);
           thisScreen.elements[id] = b;
           b.load(thisScreen);
        }

        return false;
    }

    this.normalizeBlockDim = function (){
        var blocks = thisScreen.root.querySelectorAll(".bloc"); //nodeList
        if(blocks.length == 0)
            return;

        var height = blocks[0].clientHeight;
        var firstOfLine = 0;
        var setDimUntil = function(index){
            for(var i=firstOfLine; i<index; i++){
                var b = blocks[i];
                b.style.height = height+"px";
                b.style.width = thisScreen.columnWidth+"px";
                var ds = b.querySelectorAll(".dragbar");
                ds.forEach(function(d){
                    //d.style.height = height+"px";
                    thisScreen.setUpGhostBarEvent(d);
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

    this.setUpGhostBarEvent = function(dragbar_div_element){

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

                var blocks = thisScreen.root.querySelectorAll(".bloc");
                blocks.forEach(function(b){
                        b.style.height = "auto";
                });
                thisScreen.setColumnWidth(blocWidth + (e.pageX - xInit));
        };
    };
}

}

function Context(){
    var thisContext = this;
    
    this.screen = null;

    
    this.address = window.location.origin + window.location.pathname;

    this.parseAndLoadQuery= function (query) {
        var keyVal = query.split('=');
        var key = decodeURIComponent(keyVal[0]);

        var langItem= keyVal[1].split('/');
        thisContext.screen.loadQuery(key, langItem[0], langItem[1]);
        
    }
    
    this.clearScreen = function(){
        thisContext.screen.clear();   
    }
    
    this.menuLang = "eng";

    this.loadMenu = function(lang){
        Utils.GET(lang+"/menu.html", function(text){
           var menuDom = Utils.htmlTextToDom(text); 
           var left_frame = document.getElementById("left_frame");
           
           var appendOrReplace = function(name){
               var curMenu = document.getElementById(name+"_menu")
               var newMenu = menuDom.querySelectorAll("#"+name+"_menu")[0];
               if(curMenu==null)
                   left_frame.appendChild(newMenu);
               else
                   curMenu.replace(newMenu);
           };
           
           appendOrReplace(SPELLS);
           appendOrReplace(MONSTERS);
           context.setMenu("monsters_menu");
           initMenuLinks();
        });
    }

    
    this.setMenu = function (menu_id){
        var menus = document.querySelectorAll(".menu");

        menus.forEach(function(menu){
            if(menu.id == menu_id)
                Utils.show(menu); 
            else
                Utils.hide(menu);        
        
            return false;
        });
    }

}


var context = new Context();

function asynchronizeLink(link, funOnClick){
    link.onclick = function(e) {
       var url = e.target.href.split('?');
       funOnClick(url[1]);
       //context.setURL();
       return false;
    };
}

function initMenuLinks(){
    var menuLinks = document.querySelectorAll(".menuLink"); //nodeList
    menuLinks.forEach(function(menuLink){
        asynchronizeLink(menuLink, context.parseAndLoadQuery);
    });

}

function initInterface(){
    var menuLang = document.querySelectorAll(".menuLang");
    menuLang.forEach(function(menuLink){
        asynchronizeLink(menuLink, context.loadMenu);
    });

    var buttons = document.querySelectorAll(".clearScreen");    

    buttons.forEach(function(b){
        b.onclick = function(e) {
            context.clearScreen();
            return false;
        };   
    });

    var menu_choosers = document.querySelectorAll(".menu_chooser");
    
    menu_choosers.forEach(function(mc){
        mc.onclick = function(e) {
            var url = e.target.href.split('?');
            context.setMenu(url[1]);
            return false;
        };
    });

}

window.onload = function(){

    context.loadMenu("eng");
    context.screen = new Screen("FirstScreen");
    context.screen.create(document.getElementById("right_frame"));
    initInterface();
};
