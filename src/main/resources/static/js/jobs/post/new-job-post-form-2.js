/**
 * 
 */

var app = angular.module('jobPostApp', ['moment-picker']);

app.controller('jobPostCtrl',['$scope','$http', function($scope,$http) {
	
	//model variables
	$scope.headline="";
	$scope.description = "";
	$scope.zipcode = "";
	$scope.select_distance ="";
	$scope.checkbox_travel = false;
	$scope.phone ="";
	$scope.email ="";
	
	$scope.imagesCount = 0;
	$scope.imagesMax = 4;
	$scope.isCollapsed = false;
	$scope.experience = 0;
	
	//ng-src drop downs
	$scope.availability = "dissappear";
	$scope.details = "dissappear";
	
	//set default images
	$scope.images = [
		"/images/image_icon.svg",
		"/images/image_icon.svg",
		"/images/image_icon.svg",
		"/images/image_icon.svg",
	];
	
	$scope.toggleAvailability = function(){
		$scope.availability = $scope.availability == "appear" ? "dissappear" : "appear";
	}
	
	$scope.toggleDetails = function(){
		$scope.details = $scope.details == "appear" ? "dissappear" : "appear";
	}
	
	
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