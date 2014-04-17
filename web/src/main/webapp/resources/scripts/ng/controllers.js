'use strict';

/* Controllers */

angular.module('tabletuing.controllers', ['ui.bootstrap'])
   .controller('MainCtrl', ['$scope', '$rootScope', '$resource', '$location', function ($scope, $rootScope, $resource, $location) {
	   
	   // full hierarchy
	   $scope.hierarchyItems;
	   $scope.selectedHierarchyItem = null;
	   $scope.selectedHierList = [];
	   
	   $scope.hierarchyLevels = [];
	   
	   $rootScope.selectedLocation = null;
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
	   
	   $scope.selectLocationEnabled = function() {
		   return ($scope.selectedHierList.length >= $scope.hierarchyLevels.length && $rootScope.selectedLocation == null);
	   };
	   $scope.createLocationEnabled = function() {
		   return ($scope.selectedHierList.length >= $scope.hierarchyLevels.length);
	   };
	   $scope.clearLocationEnabled = function() {
		   return ($rootScope.selectedLocation !== null);
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
	   
	   $scope.createLocation = function(parentExtId) {
		   console.log("Create location for parentExtId=" + parentExtId);
		   $location.search('parentExtId', parentExtId).path('/form/location');
	   }
	   $scope.clearLocation = function() {
		   $rootScope.selectedLocation = null;
	   }
	   
	   $scope.go = function ( path ) {
		   $location.path( path );
	    };
	   
	   var locationHierarchyResource = $resource(contextPath + '/api/rest/locationhierarchies/:path');
	    
	   var init = function () {
		   var fullHierarchy = locationHierarchyResource.get();
		   fullHierarchy.$promise.then(function (result) {
		       $scope.hierarchyItems = result.locationHierarchies;
		   });
		   
		   var hierarchyLevels = locationHierarchyResource.query({path:'levels'})
		   		.$promise.then(
				   function (result) {
					   $scope.hierarchyLevels = result;
				   }
				 );
		   
	   };  
	   
	   init();	   
   }])
//  .controller('MyCtrl1', [function() {
//
//  }])
//  .controller('MyCtrl2', [function() {
//
//  }])
  .controller('LocationCtrl', ['$scope', '$rootScope', '$resource', '$location', '$modal', function ($scope, $rootScope, $resource, $location, $modal) {  
	   
	  $scope.parentLocationHierarchy = $scope.selectedHierarchyItem;
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
        
	  	// location resource
        
       
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
        var locationHierarchyResource = $resource(contextPath + '/api/rest/locationhierarchies/:extId');

        $scope.open = function (title, items) {
          var modalInstance = $modal.open({
            templateUrl: 'partials/modalContent.html',
            controller: 'ModalInstanceCtrl',
            resolve: {
            	title: function () {
            		return title;
            	},
                items: function () {
                	return items;
                }
            }
                 
          });
        };
        
        var locationResource = $resource(contextPath + '/api/rest/locations/:extId');
        $scope.createLocation = function() {
        	$scope.newLocation.locationLevel = $scope.parentLocationHierarchy;
        	
        	locationResource.save($scope.newLocation).$promise.then(
        		function(locationResource) {
        			$rootScope.selectedLocation = locationResource;
        			$location.path("/home");
        		},
        		function(error) {
        			if (error.status == '400') {
        				$scope.open('Create Location validation failed', error.data.errors);
        			} else {
        				$scope.open('Error ' + error.status);
        			}
        			
        		});
        };
        
 	   $scope.cancelLocation = function(parentExtId) {
 		   $location.path('/home');
	   }
        
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
  .controller('ModalInstanceCtrl', ['$scope', '$modalInstance', 'title', 'items', function($scope, $modalInstance, title, items) {
    	 $scope.modalTitle = title;
    	 $scope.modalItems = items;
	  $scope.ok = function () {
	    $modalInstance.close();
	  };
  }])
;


