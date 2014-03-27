'use strict';

/* Controllers */

angular.module('tabletuing.controllers', []).
   controller('MainCtrl', ['$scope', '$location', '$resource', function ($scope, $location, $resource) {
	   
	   // full hierarchy
	   $scope.hierarchyItems;
	   $scope.selectedHierachyItem = null;
	   var selectedHierList = [];
	   
	   $scope.hierarchyLevels = [];
	   
	   $scope.location;
	   $scope.individual;
	   
	   // default to root
	   $scope.parentExtId = 'HIERARCHY_ROOT';
	   
	   $scope.getLocation = function(index) {
		   var location = null;
		   if (index < $scope.hierarchyLevels.length && selectedHierList[index] != null) {
			   location = selectedHierList[index];
		   }
		   return location;
	   };
	   $scope.disableLevel = function(index) {
		   return !(index == selectedHierList.length);
	   };
	   $scope.disableLocation = function() {
		   return selectedHierList.length < $scope.hierarchyLevels.length;
	   };
	   
	   $scope.byParent = function() {
		   return function( item ) {
			   return (item.parent.extId == $scope.parentExtId);
		   };
	   };
	   
	   $scope.selectHierarchyItem = function(selectedItem) {
		   //console.log("Setting selected hierarchy item to " + selectedItem.name);
		   selectedHierList.push(selectedItem);
		   $scope.selectedHierarchyItem = selectedItem;
		   $scope.parentExtId = selectedItem.extId;
	   }
	   
	   var locationHierarchyResource = $resource('/openhds2/api/rest/locationhierarchies/:path');
	    
	   $scope.init = function () {
		   var fullHierarchy = locationHierarchyResource.get();
		   fullHierarchy.$promise.then(function (result) {
		       $scope.hierarchyItems = result.locationHierarchies;
		   });
		   
		   var hierarchyLevels = locationHierarchyResource.query({path:'levels'})
		   .$promise.then(function (result) {
			   console.log(result);
			   $scope.hierarchyLevels = result;
		   });
		   
	   };  
	   
	   $scope.go = function ( path ) {
		   $location.path( path );
	    };
	   
   }])
//  .controller('MyCtrl1', [function() {
//
//  }])
//  .controller('MyCtrl2', [function() {
//
//  }])
  .controller('locationCtrl', ['$scope', '$resource', function ($scope, $resource) {
	        $scope.master = {};
	   
//	        $scope.createLocation = function() {
//	          $scope.master = angular.copy($scope.location);
//	        };
	   
	        $scope.reset = function() {
	          $scope.location = angular.copy($scope.master);
	        };
	   
	        $scope.resetAll = function() {
	          $scope.master = {};
	          $scope.location = angular.copy($scope.master);
	          $scope.locationForm.$setPristine();
	        };
	        
	        $scope.reset();
	        
	        var locationResource = $resource('/openhds2/api/rest/locations/:extId');
	        
	        $scope.getLocations = function() {
	        	locationResource.get({}, function(locationResource) {
	        		$scope.locations = locationResource;
	        	});
	        	
	        };
	        
	        $scope.getLocation = function(id) {
	        	locationResource.get({extId:id}, function(locationResource) {
	        		$scope.location = locationResource;
	        		console.log("Get locations " + locationResource);
	        	});
	        	console.log("Get location with id " + id);
	        };
	        
	        $scope.createLocation = function() {
	        	locationResource.save($scope.location, function(locationResource) {
	        		console.log("Location created");
	        	});
	        };
  }])
//  .controller('HomeCtrl', [function() {
//
//  }])
  ;