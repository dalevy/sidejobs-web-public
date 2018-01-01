/**
 * 
 */

var app = angular.module('jobPostApp', []);

app.controller('jobPostCtrl',['$scope','$http', function($scope,$http) {
		
	$scope.imagesCount = 0;
	$scope.imagesMax = 4;
	$scope.isCollapsed = false;
	
	$scope.collapse = "site-collapsable";
	
	//set default images
	$scope.images = [
		"/images/image_icon.svg",
		"/images/image_icon.svg",
		"/images/image_icon.svg",
		"/images/image_icon.svg",
	];
	
	//set image ng-src after base64 conversion
	$scope.finish = function(base64,id){
		$scope.$apply(function(){
			$scope.images[id]= base64;
			$scope.imagesCount++;
		});
	
	}
	
	$scope.uploadFile = function(input,id){
		console.log(id);
		encodeImageFileAsURL(input,$scope.finish,id);
	}
	
				  
}]);