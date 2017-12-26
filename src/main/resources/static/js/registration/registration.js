/**
 * Registration: /workers/registration
 */

/**
$(function () {
  $('[data-toggle="popover"]').popover()
});

$('.popover-dismiss').popover({
	  trigger: 'focus'
});
**/

var app = angular.module('registrationApp', []);

app.service('JobsService',function($http){
	
	this.listCategories = function(){
		return $http({
		    method: 'GET',
		    url: API_LIST_CATEGORIES
		}).then(function (response, status, headers, config) {
			return response.data;
		}).catch(function (error) {    
			throw error;
		});
	}
	
	this.registerWorker = function(mail,pword,fname,lname,cat){
		return $http({
		    method: 'POST',
		    url: API_REGISTER_WORKER,
		    params:{
		    	email: mail,
		    	password: pword,
		    	firstname: fname,
		    	lastname: lname,
		    }
		}).then(function (response, status, headers, config) {
			return response.data;
		}).catch(function (error) {    
			throw error;
		});
	}
	
});


app.controller('registrationCtrl',['$scope','$http','JobsService', function($scope,$http,JobsService) {


	$scope.email = "";
	$scope.firstname = "";
	$scope.lastname = "";
	$scope.password = "";
	$scope.confirm_password ="";
	$scope.hideLoading = true;
	
	
	
	$scope.submit = function(){
		
		$scope.hideLoading = false;
		
		JobsService.registerWorker($scope.email,$scope.password,$scope.firstname,$scope.lastname,$scope.category)
			.then(function(response){
				console.log(response);
				$scope.hideLoading = true;
				if(response.status = "Success")
				{
					
					location.replace("/notification/registration/verification");
				}
		});
	}
	
	
		  
}]);