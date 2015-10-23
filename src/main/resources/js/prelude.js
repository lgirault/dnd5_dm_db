
NodeList.prototype.forEach = Array.prototype.forEach
HTMLCollection.prototype.forEach = Array.prototype.forEach

Object.prototype.forEach = function(f){
    for (var property in this) {
        if (this.hasOwnProperty(property)) {
            f(property, this[property]);
        }
    }    
}
Object.prototype.jsonString = function(){
    return JSON.stringify(this);
}

Object.cast = function(rawObj, constructor){
    var obj = new constructor();
    for(var i in rawObj)
        obj[i] = rawObj[i];
    return obj;
}


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

Element.prototype.replace = function(newElt){
     this.parentElement.insertBefore(newElt, this);
     this.remove();
}




Array.prototype.contains = function (elt){
    return this.indexOf(elt)!=-1;
}
Array.prototype.remove = function(elt){
    var index = this.indexOf(elt);
    if(index!=-1){
        this.splice(index, 1);
    }
}