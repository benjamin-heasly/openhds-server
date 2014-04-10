'use strict';

/* Controllers */

angular.module('tabletuing.controllers', []).
   controller('MainCtrl', ['$scope', '$resource', '$location', function ($scope, $resource, $location) {
	   
	   // full hierarchy
	   $scope.hierarchyItems;
	   $scope.selectedHierarchyItem = null;
	   $scope.selectedHierList = [];
	   
	   $scope.hierarchyLevels = [];
	   
	   //$scope.location;
	   $scope.individual;
	   
	   // default to root
	   $scope.parentExtId = 'HIERARCHY_ROOT';
	   
	   $scope.getLocation = function(index) {
		   var location = null;
		   if (index < $scope.hierarchyLevels.length && $scope.selectedHierList[index] != null) {
			   location = $scope.selectedHierList[index];
		   }
		   return location;
	   };
	   $scope.disableLevel = function(index) {
		   return !(index == $scope.selectedHierList.length);
	   };
	   $scope.disableLocation = function() {
		   return $scope.selectedHierList.length < $scope.hierarchyLevels.length;
	   };
	   
	   $scope.byParent = function() {
		   return function( item ) {
			   return (item.parent.extId == $scope.parentExtId);
		   };
	   };
	   
	   $scope.selectHierarchyItem = function(selectedItem) {
		   //console.log("Setting selected hierarchy item to " + selectedItem.name);
		   $scope.selectedHierList.push(selectedItem);
		   $scope.selectedHierarchyItem = selectedItem;
		   $scope.parentExtId = selectedItem.extId;
	   }
	   
	   var locationHierarchyResource = $resource(contextPath + '/api/rest/locationhierarchies/:path');
	    
	   $scope.init = function () {

		   
		   var fullHierarchy = locationHierarchyResource.get();
		   fullHierarchy.$promise.then(function (result) {
		       $scope.hierarchyItems = result.locationHierarchies;
		   });
		   
		   var hierarchyLevels = locationHierarchyResource.query({path:'levels'})
		   .$promise.then(function (result) {
			   $scope.hierarchyLevels = result;
		   });
		   
	   };  
	   
	   $scope.createLocation = function(parentExtId) {
		   console.log("Create location for parentExtId=" + parentExtId);
		   $location.search('parentExtId', parentExtId).path('/form/location');
	   }
	   
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
  .controller('LocationCtrl', ['$scope', '$resource', '$location', function ($scope, $resource, $location) {  
	   
	 
	  //$scope.master = {};
   
//	        $scope.createLocation = function() {
//	          $scope.master = angular.copy($scope.location);
//	        };
   
//        $scope.reset = function() {
//          $scope.location = angular.copy($scope.master);
//        };
//   
//        $scope.resetAll = function() {
//          $scope.master = {};
//          $scope.location = angular.copy($scope.master);
//          $scope.locationForm.$setPristine();
//        };
        
       // $scope.reset();
        
        var locationResource = $resource(contextPath + '/api/rest/locations/:extId');
        $scope.createLocation = function() {
        	locationResource.save($scope.location, function(locationResource) {
        		console.log("Location created");
        	});
        };
//        $scope.getLocations = function() {
//        	locationResource.get({}, function(locationResource) {
//        		$scope.locations = locationResource;
//        	});
//        	
//        };
        
//        $scope.getLocation = function(id) {
//        	locationResource.get({extId:id}, function(locationResource) {
//        		$scope.location = locationResource;
//        		console.log("Get locations " + locationResource);
//        	});
//        	console.log("Get location with id " + id);
//        };
//        
//        $scope.createLocation = function() {
//        	locationResource.save($scope.location, function(locationResource) {
//        		console.log("Location created");
//        	});
//        };
        $scope.parentLocationHierarchy = $scope.selectedHierarchyItem;
        var locationHierarchyResource = $resource(contextPath + '/api/rest/locationhierarchies/:extId');
        
       	var init = function() {
       		var parentExtId = ($location.search()).parentExtId;
       		
       		// route back to home
       		if (!parentExtId) {
       			$location.path("/home");
       		}
       		// load parent
       		else if ($scope.parentLocationHierarchy === null || ($scope.parentLocationHierarchy.extId !== ($location.search()).parentExtId)) {
       			$scope.parentLocationHierarchy = locationHierarchyResource.get({extId:parentExtId});
       		}
        	
        };
        
        init();
  }])
//  .controller('HomeCtrl', [function() {
//
//  }])
  ;


