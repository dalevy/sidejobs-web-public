/**
 * 
 */

var app = angular.module('jobPostApp', []);

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
	
	this.listAreasByCategory = function(category_id){
		return $http({
		    method: 'GET',
		    url: API_LIST_AREAS_BY_CATEGORY,
		    params:{
		    	id: category_id,
		    }
		}).then(function (response, status, headers, config) {
			return response.data;
		}).catch(function (error) {    
			throw error;
		});
	}
	
	this.listSpecialtiesByArea = function(area_id){
		return $http({
		    method: 'GET',
		    url: API_LIST_SPECIALTIES_BY_AREA,
		    params:{
		    	id: area_id,
		    }
		}).then(function (response, status, headers, config) {
			return response.data;
		}).catch(function (error) {    
			throw error;
		});
	}
	
	this.listSpecialtiesByAreaAndCategory = function(area_id,category_id){
		return $http({
		    method: 'GET',
		    url: API_LIST_SPECIALTIES_BY_AREA_CATEGORY,
		    params:{
		    	area: area_id,
		    	category: category_id,
		    }
		}).then(function (response, status, headers, config) {
			return response.data;
		}).catch(function (error) {    
			throw error;
		});
	}
});

app.controller('jobPostCtrl',['$scope','$http','JobsService', function($scope,$http,JobsService) {


	$scope.categories = [];
	$scope.areas = [];
	$scope.areasSelected = {};//holds the actual selected item
	$scope.areaIsGreen = [];//for toggling the active class
	$scope.specialties = [];
	
	$scope.isSelectedCategory = false;
	$scope.isSelectedArea = false;
    $scope.isSelectedSpecialty = false;

	
	JobsService.listCategories().then(function(response){
		$scope.categories = response.data;	
	});
	
	$scope.categoryChange = function(){
		$scope.isSelectedCategory = true;
		
		//hide specialties
		if($scope.isSelectedArea == true)
			$scope.isSelectedArea = false;
		
		//hide continue button
		$scope.isSelectedSpecialty = false;
		
		JobsService.listAreasByCategory($scope.select_category.id).then(function(response){
			console.log(response);
			$scope.areas = response.data;
		});
	}
	
	$scope.areaChange = function(){
		$scope.areaIsGreen = [];
		//empty specialties dictionary
		$scope.areasSelected = {};
		
		//empty model due to refill of select options by category function
		if($scope.select_area== null)
			return;
		
		//hide continue button
		$scope.isSelectedSpecialty = false;
		
		JobsService.listSpecialtiesByAreaAndCategory($scope.select_area.id,$scope.select_category.id).then(function(response){
			console.log(response);
			$scope.specialties = [];

			$scope.isSelectedArea = true;
			$scope.specialties = response.data;
			
			angular.forEach(response.data, function(value, key) {
				$scope.areasSelected[value.id] = false;
			});
		})
	}
	
	//change the selected specialty to green and set it to selected
	$scope.specialtySelected = function(index,id){
		 $scope.areaIsGreen[index] =  $scope.areaIsGreen[index] == 'list-group-item-success' ? '': 'list-group-item-success';
		
		 //remove from map as necessary
		 var remove = $scope.areaIsGreen[index] == 'list-group-item-success' ? false : true;
		 if(remove)
		     $scope.areasSelected[id] = false;
		 else
			 $scope.areasSelected[id] = true;
		 
		 var atLeastOneTrue = false;
		 
		 angular.forEach($scope.areasSelected, function(value,key){
			if(value == true)
				atLeastOneTrue = true;
			console.log("turn: "+value+" key: "+key);
		 });
		 
		 $scope.isSelectedSpecialty = atLeastOneTrue;
		 console.log("at least one is : "+atLeastOneTrue)
		 
	}
	
	$scope.next = function (){
		var specialties ="";
		 angular.forEach($scope.areasSelected, function(value,key){
			 if(value == true)
				 specialties = specialties + key + ",";

		});
		 
		 var params = {
			'category':$scope.select_category.id,
			'area':$scope.select_area.id,
			'specialties':specialties.substring(0,specialties.length-1),
		 };
		 
		 console.log(buildUrlParameters(params));
		 
		 window.location = "/jobs/post/2"+buildUrlParameters(params);
	}
	
		  
}]);