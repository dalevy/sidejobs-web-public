/**
 * Login
 */


var app = angular.module('loginApp', []);

app.service('UserService',function($http){
	
	this.loginUser = function(mail,pword){
		return $http({
		    method: 'POST',
		    url: API_LOGIN_USER,
		    params:{
		    	email: mail,
		    	password: pword,
		    }
		}).then(function (response, status, headers, config) {
			return response.data;
		}).catch(function (error) {    
			throw error;
		});
	}
	
});


app.controller('loginCtrl',['$scope','$http','UserService', function($scope,$http,UserService) {

	$scope.email = "";
	$scope.password = "";
	$scope.error_message = "adsfadfadf";
	$scope.hideError = true;
	$scope.hideLoading = true;
	
	$scope.submit = function(){
		$scope.hideLoading = false;
		UserService.loginUser($scope.email,$scope.password)
			.then(function(response){
				$scope.hideLoading = true;
			 var status = response.data;
			 console.log(response);
			 if(status.code === "Failure")
			 {
				 $scope.hideError = false;
				 $scope.error_message = status.message;
			 }
			 
			 location.replace("/");
		});
	}
	
	$scope.clear = function(){
		$scope.email = "";
		$scope.password = "";
	}
	
		  
}]);