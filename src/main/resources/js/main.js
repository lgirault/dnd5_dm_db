
var Keyboard = {
    enter : 13
}

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

function setIndex(htmlText){
    var htmlDom = Utils.htmlTextToDom(htmlText);
    var index_wrapper = document.getElementById("index_wrapper");
    index_wrapper.children.remove();
    htmlDom.querySelectorAll("#indexBloc").forEach(function(n){
        index_wrapper.appendChild(n);
    });
    linkifyItems(index_wrapper);
    Utils.show(index_wrapper);
    document.querySelectorAll(".menu").forEach(Utils.hide);
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

var AppendBlock = {monsters : appendMonsterBlock, spells : appendSpellBlock};

function Bloc(type, lang, id){
    var thisBloc = this;
    this.id = id;
    this.lang = lang;
    this.type = type;

    this.relativeAddress = function(){
       return "/" + this.type + "/" + this.lang + "/" + this.id + ".html"   
    }

    this.load = function(screen){
        Utils.GET(thisBloc.relativeAddress(), AppendBlock[thisBloc.type].bind(null, screen));
        return false;
    }
}

function Screen(screen_id){
    var thisScreen = this;
    this.id = screen_id;
    this.columnWidth = 200;
    this.elements = {};
    this.root = null;

    this.setColumnWidth = function(newWidth){
        thisScreen.columnWidth = newWidth;
        thisScreen.normalizeBlockDim();    
    }
        
    this.remove = function(){
        thisScreen.root.remove();
    }
    this.create = function(container){
        thisScreen.root = document.createElement("div");
        

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

    
    
    this.loadQuery = function(type, lang, id){
        
        var b = thisScreen.elements[id];
        if(Utils.isDefined(b) && b.lang != lang){
           b.lang = lang;
           b.load(thisScreen);
           thisScreen.store();
        }
           
        if(!Utils.isDefined(b)) {
           b = new Bloc(type, lang, id);
           thisScreen.elements[id] = b;
           b.load(thisScreen);
           thisScreen.store();
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
    this.store = function(){
        localStorage.setItem(thisScreen.id, thisScreen.jsonString());
    }
    this.deleteFromStorage=function(){
        localStorage.removeItem(thisScreen.id);
    }
}

function Context(){
    var thisContext = this;
    
    this.screen = null;
    
    this.newScreenId = 0;
    this.screenList = [];
    
    this.setScreen = function(s){
        if(thisContext.screen != null){
           thisContext.screen.store(); 
           thisContext.screen.remove();
        }

        thisContext.screen = s;

        s.create(document.getElementById("right_frame"));
        thisContext.displayScreenList();
    }

    this.setScreenName = function(name){
        var oldName = thisContext.screen.id;
        localStorage.removeItem(oldName);
        thisContext.screen.id = name;
        var index = thisContext.screenList.indexOf(oldName);
        thisContext.screenList[index] = name;
        thisContext.displayScreenList(); 
        thisContext.storeScreenList();
        thisContext.screen.store(); 
    }

    this.deleteScreen = function(){
        thisContext.deleteScreenWithId(thisContext.screen.id);
    }

    this.deleteScreenWithId = function(screenId){
        var index = thisContext.screenList.indexOf(screenId);
        thisContext.screenList.remove(screenId);
        
        var deleteDisplayedScreen = screenId == thisContext.screen.id;
        thisContext.storeScreenList();
        
        if(deleteDisplayedScreen){
           if(thisContext.screenList.length == 0)
               thisContext.createScreen();
           else {
               var newShown = index - 1;
               if(newShown < 0)
                newShown = 0;
               thisContext.loadScreen(thisContext.screenList[newShown]); 
           }
        }
    }

    this.createScreen = function(){
        var screenName = "Screen"+ thisContext.newScreenId;
        thisContext.newScreenId++;

        if(thisContext.screenList.contains(screenName))
            return thisContext.createScreen();
        
         var s = new Screen(screenName);
         thisContext.screenList.push(s.id);
         thisContext.storeScreenList();

         thisContext.setScreen(s);
         s.store();

    }
    this.loadScreen = function(screenId){
        var jsonScreen = JSON.parse(localStorage.getItem(screenId));
        var s = Object.cast(jsonScreen, Screen);
        s.elements.forEach(function(bid, jsonBloc){
            s.elements[bid]=Object.cast(jsonBloc, Bloc);
        });
        thisContext.setScreen(s);
    }
    

    this.displayScreenList = function(){
       var createTitleNode = function(title){
            var titleNode = document.createElement("div");
            var aNode = document.createElement("a");
            
            //aNode.href="?"+title;
            aNode.onclick = function(e){
               thisContext.loadScreen(title);
               return false;  
            };

            aNode.appendChild(document.createTextNode(title));
            titleNode.appendChild(aNode);
            //thisScreen.root.appendChild(titleNode);
            
            return titleNode;
        }

        var pannelList = document.getElementById("pannelList");
        pannelList.childNodes.remove();
        thisContext.screenList.forEach(function(sid){
            var tnode = createTitleNode(sid);
            pannelList.appendChild(tnode);
            if(thisContext.screen.id == sid){
                   tnode.className="selected";
            }
        });

    }

    this.loadScreenList = function(){
        var storedScreenList = localStorage.getItem("screenList");
        if(storedScreenList!= null)
            thisContext.screenList = JSON.parse(storedScreenList);
    }
    this.storeScreenList = function(){
        localStorage.setItem("screenList", JSON.stringify(thisContext.screenList));
    }

    this.address = window.location.origin + window.location.pathname;

    this.parseAndLoadQuery= function (query) {
        var keyVal = query.split('=');
        var key = decodeURIComponent(keyVal[0]);
        var langItem= keyVal[1].split('/');
        
        if(key == "indexes"){
            var indexAddr = "/" + key + "/" + langItem[0] + "/" + langItem[1] + ".html";
            Utils.GET(indexAddr, setIndex);
        }
        else
            thisContext.screen.loadQuery(key, langItem[0], langItem[1]);
        //console.log(thisContext.screen.jsonString());
           
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
               var curMenu = document.getElementById(name)
               var newMenu = menuDom.querySelectorAll("#"+name)[0];
               if(curMenu==null)
                   left_frame.appendChild(newMenu);
               else
                   curMenu.replace(newMenu);
           };
           
           appendOrReplace(SPELLS+"_menu");
           appendOrReplace(MONSTERS+"_menu");
           appendOrReplace(SPELLS+"_indexes");
           appendOrReplace(MONSTERS+"_indexes");
           context.setMenu("monsters_menu");
           initMenuLinks();
        });
    }

    
    this.setMenu = function (menu_id){
        var menus = document.querySelectorAll(".menu");
        Utils.hideEltWithId("index_wrapper");
        menus.forEach(function(menu){
            if(menu.id == menu_id)
                Utils.show(menu); 
            else
                Utils.hide(menu);        
        
            return false;
        });
    }

    this.search = function(str){
        var items = document.querySelectorAll(".itemLink");
        if(str.length == 0)
            items.forEach(function(a){
                Utils.show(a);   
            });
        else
            items.forEach(function(a){
                var score = str.score(a.text, 1);
                if(score >= 0.5)
                    Utils.show(a);   
                else
                    Utils.hide(a);   
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

function linkifyItems(root){
    var menuLinks = root.querySelectorAll(".itemLink");
    menuLinks.forEach(function(menuLink){
        asynchronizeLink(menuLink, context.parseAndLoadQuery);
    });
}

function initMenuLinks(){
    linkifyItems(document)
}

function initInterface(){
    var menuLang = document.querySelectorAll(".menuLang");
    menuLang.forEach(function(menuLink){
        asynchronizeLink(menuLink, context.loadMenu);
    });

    var onClickForId = function(id, f){
        document.getElementById(id).onclick = function(e){f()};   
    }

    onClickForId("clearScreen", context.clearScreen);   
    onClickForId("createScreen", context.createScreen);
    onClickForId("deleteScreen", context.deleteScreen);
    onClickForId("renameScreen", function(){
       var selected = document.querySelectorAll("#pannelList .selected a"); 
       var input = document.createElement("input");
       input.type="text";
       input.value=selected[0].text;
       input.style.width = (selected[0].offsetWidth+20)+"px";
       selected[0].replace(input);
       input.onkeypress = function(e){
         if(e.keyCode == Keyboard.enter){
            context.setScreenName(input.value);  
            return false;
         }
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

    var searchInput = document.getElementById("search");

    searchInput.oninput = function(e){
        context.search(e.srcElement.value);
    };


    context.loadScreenList();
    if(context.screenList.length == 0)
        context.createScreen();
    else    
        context.loadScreen(context.screenList[0]);
   
}

window.onload = function(){

    context.loadMenu("eng");
    initInterface();
};
