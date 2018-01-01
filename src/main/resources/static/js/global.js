

var API_LIST_CATEGORIES = "/jobs/list/categories";
var API_REGISTER_WORKER = "/user/registration/worker";
var API_LIST_AREAS_BY_CATEGORY = "/jobs/list/areas/category";
var API_LIST_SPECIALTIES_BY_AREA = "/jobs/list/specialties/area";
var API_LIST_SPECIALTIES_BY_AREA_CATEGORY = "/jobs/list/specialties/area/category";
var API_LOGIN_USER = "/user/login";

//name length between 4 and 25 characters no numbers or symbols
var REGEX_NAME_RULES = "^[a-zA-Z]{4,25}$";

var buildUrlParameters = function buildUrlParameters(params){
	
	 var urlParams = "";
	 var count = 0;
	 angular.forEach(params, function(value,key){
		 if(count < 1)
			 urlParams = "?"+key+"="+value;
		 else
			 urlParams = urlParams + "&"+key+"="+value;
		 
		 count++;
	 });
	return urlParams;
};

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;
    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

function encodeImageFileAsURL(element, callback, elementId) {
	  var file = element.files[0];
	  var reader = new FileReader();
	  var base64 = "";
	  reader.onloadend = function() {
	      //console.log('RESULT', reader.result)
		  callback(reader.result,elementId);
	  }
	  reader.readAsDataURL(file);
}


//From https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys
if (!Object.keys) {
  Object.keys = (function() {
    'use strict';
    var hasOwnProperty = Object.prototype.hasOwnProperty,
        hasDontEnumBug = !({ toString: null }).propertyIsEnumerable('toString'),
        dontEnums = [
          'toString',
          'toLocaleString',
          'valueOf',
          'hasOwnProperty',
          'isPrototypeOf',
          'propertyIsEnumerable',
          'constructor'
        ],
        dontEnumsLength = dontEnums.length;

    return function(obj) {
      if (typeof obj !== 'function' && (typeof obj !== 'object' || obj === null)) {
        throw new TypeError('Object.keys called on non-object');
      }

      var result = [], prop, i;

      for (prop in obj) {
        if (hasOwnProperty.call(obj, prop)) {
          result.push(prop);
        }
      }

      if (hasDontEnumBug) {
        for (i = 0; i < dontEnumsLength; i++) {
          if (hasOwnProperty.call(obj, dontEnums[i])) {
            result.push(dontEnums[i]);
          }
        }
      }
      return result;
    };
  }());
}