'use strict';

/* Controllers */

angular.module('tabletuing.controllers', ['ui.bootstrap'])
   .controller('MainCtrl', ['$scope', '$rootScope', '$resource', '$location', function ($scope, $rootScope, $resource, $location) {
	   
	   // full hierarchy
	   $scope.hierarchyItems;
	   $scope.selectedLevel = null;
	   $scope.selectedHierList = [];
	   
	   // supported levels (Region, SubRegion, Village, etc.)
	   $scope.hierarchyLevels = [];
	   
	   // selected objects used in multiple controllers
	   $rootScope.selectedLocation = null;
	   $rootScope.selectedHousehold = null;
	   $rootScope.selectedIndividual = null;
	   
	   $rootScope.locations = [];
	   $rootScope.households = [];
	   
	   var HIERARCHY_ROOT = 'HIERARCHY_ROOT';
	   // default to root
	   $scope.parentExtId = HIERARCHY_ROOT;
	   
	   // TODO move to reusable service
	   var locationResource = $resource(contextPath + '/api/rest/locations/locationLevel/:locationLevel',
			   {locationLevel : "@locationLevel"},
			   {
				   getLocationsForLevel: {method: "GET"}
			   });
	   
	   $scope.getLevel = function(index) {
		   var location = null;
		   if (index < $scope.hierarchyLevels.length && $scope.selectedHierList[index] != null) {
			   location = $scope.selectedHierList[index];
		   }
		   return location;
	   };
	   $scope.disableLevel = function(index) {
		   //console.log("index " + index + " selected hier length " + $scope.selectedHierList.length);
		   return index >= $scope.selectedHierList.length;
	   };
	   $scope.levelSelected = function(index) {
		   // TODO move to reset function
		   // reset child types
		   $rootScope.selectedLocation = null;
		   $rootScope.locations = [];
		   $rootScope.selectedHousehold = null;
		   $rootScope.households = [];
		   $rootScope.selectedIndividual = null;
		   
		   // pop off selected level
		   var levelsToRemove = $scope.selectedHierList.length - index;
		   while (levelsToRemove > 0) {
			   $scope.selectedHierList.pop();
			   levelsToRemove--;
		   }
		   
		   // reset selected level
		   if ($scope.selectedHierList.length > 0) {
		   		$scope.selectedLevel = $scope.selectedHierList[$scope.selectedHierList.length - 1];
		  		$scope.parentExtId = $scope.selectedLevel.extId;
		   } else {
			   $scope.selectedLevel = null;
			   $scope.parentExtId = HIERARCHY_ROOT;
		   }
		   
	   }
	   $scope.selectLocation = function() {
		   locationResource.getLocationsForLevel({locationLevel : $scope.selectedLevel.uuid})
	   			.$promise
	   			.then(function(result) {
	   				$rootScope.locations = result.data.locations;
	   			});
		   $rootScope.selectedLocation = null;
	   }
	   $scope.selectLocationItem = function(loc) {
		   $rootScope.selectedLocation = loc;
		   $rootScope.locations = [];
	   }
	   
	   $scope.disableLocation = function() {
		   return ($scope.selectedHierList.length < $scope.hierarchyLevels.length || $rootScope.selectedLocation == null);
	   };
	   $scope.disableHousehold = function() {
		   return ($scope.selectedHierList.length < $scope.hierarchyLevels.length || $rootScope.selectedHousehold == null);
	   };
	   $scope.showCreateLocationButton = function() {
		   return ($scope.selectedHierList.length >= $scope.hierarchyLevels.length);
	   };
	   $scope.showCreateHouseholdButton = function() {
		   return ($scope.selectedHierList.length >= $scope.hierarchyLevels.length && $rootScope.selectedLocation !== null);
	   };
//	   $scope.showCreateHeadOfHouseholdButton = function() {
//		   return ($rootScope.selectedLocation != null && $rootScope.selectedHousehold != null && $rootScope.selectedHousehold.groupHead == null);
//	   };
	   $scope.showCreateIndividualButton = function() {
		   return ($rootScope.selectedLocation != null && $rootScope.selectedHousehold != null); // && $rootScope.selectedHousehold.groupHead != null);
	   };
	   $scope.showCreateRelationshipButton = function() {
		   return false;
	   };
//	   
	  // $scope.clearLocationEnabled = function() {
//		   return ($rootScope.selectedLocation !== null);
//	   };
	   
	   $scope.byParent = function() {
		   return function( item ) {
			   return (item.parent.extId == $scope.parentExtId);
		   };
	   };
	   
	   $scope.selectLevelItem = function(selectedItem) {
		   //console.log("Setting selected hierarchy item to " + selectedItem.name);
		   $scope.selectedHierList.push(selectedItem);
		   $scope.selectedLevel = selectedItem;
		   $scope.parentExtId = selectedItem.extId;
		   
		   // load locations if last level selected
		   if ($scope.selectedHierList.length >= $scope.hierarchyLevels.length) {
			   locationResource.getLocationsForLevel({locationLevel : $scope.selectedLevel.uuid})
			   		.$promise
			   		.then(function(result) {
			   			$rootScope.locations = result.data.locations;
			   		 });
		   }
	   }
	   
	   $scope.createLocation = function(parentExtId) {
		   console.log("Create location for parentExtId=" + parentExtId);
		   $location.search('parentExtId', parentExtId).path('/form/location');
	   }
	   $scope.createHousehold = function() {
		   $location.path("/form/household");
	   }
	   $scope.createIndividual = function() {
		   $locaion.path("/form/individual");
	   }
	   
	   $scope.go = function ( path ) {
		   $location.path( path );
	    };
	    
	   var locationHierarchyResource = $resource(contextPath + '/api/rest/locationhierarchies/:path', {}, 
			   { 
		   		 getLocationLevels : {method: 'GET', isArray: false}, 
		   		 getLevels : {method: 'GET', isArray: false}
			   }
	   ); 
	   var init = function () {
		   locationHierarchyResource.getLocationLevels().$promise.then(function (result) {
		       $scope.hierarchyItems = result.data.locationHierarchies;
		   });
		   
		   var hierarchyLevels = locationHierarchyResource.getLevels({'path':"levels"})
		   		.$promise.then(
				   function (result) {
					   $scope.hierarchyLevels = result.data.locationLevels;
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
	   
	  $scope.parentLocationHierarchy = $scope.selectedLevel;

        var locationHierarchyResource = $resource(contextPath + '/api/rest/locationhierarchies/:extId');

        $scope.openModal = function (title, items) {
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
        			$rootScope.selectedLocation = locationResource.data.location;
        			$location.path("/home");
        		},
        		function(error) {
        			var title, items;
        			if (error.status == '400') {
        				title = error.data.resultMessage;
        				if (error.data.resultCode == '2') {
        					items = error.data.data.constraintViolations;
        				}
        			} else {
        				title = 'Error ' + error.status;
        			}
        			
        			$scope.openModal(title, items);
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
       			locationHierarchyResource.get({extId:parentExtId})
       				.$promise
       				.then(
       	        		function(locationHierarchyResource) {
       	        			$scope.parentLocationHierarchy = locationHierarchyResource.data.location;
       	        		},
       	        		function(error) {
       	        			title = 'Error ' + error.status;
       	        			$scope.openModal(title, null);
       	        		});
       		}
        	
        };
        
        init();          
  }])
  .controller('HouseholdCtrl', ['$scope', '$rootScope', '$resource', '$location', '$modal', function ($scope, $rootScope, $resource, $location, $modal) {  
	  var householdResource = $resource(contextPath + '/api/rest/socialgroups/:extId');
      
	  // TODO move to module
      $scope.openModal = function (title, items) {
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
	  
	  $scope.createNewHousehold = function() {
      	householdResource.save($scope.newHousehold).$promise.then(
      		function(householdResource) {
      			$rootScope.selectedHousehold = householdResource;
      			$location.path("/home");
      		},
      		// duplicated from create location
    		function(error) {
    			var title, items;
    			if (error.status == '400') {
    				title = error.data.resultMessage;
    				if (error.data.resultCode == '2') {
    					items = error.data.data.constraintViolations;
    				}
    			} else {
    				title = 'Error ' + error.status;
    			}
    			
    			$scope.openModal(title, items);
    		});
      };
      
	   $scope.cancelNewHousehold = function() {
		   $location.path("/home");
	   }
      
	  var init = function() {
     		
      	
      };
      
      init();          
  }])
    .controller('IndividualCtrl', ['$scope', '$rootScope', '$resource', '$location', '$modal', function ($scope, $rootScope, $resource, $location, $modal) {  
	 
       	var init = function() {
       		
        	
        };
        
        init();          
  }])
    .controller('MarriageCtrl', ['$scope', '$rootScope', '$resource', '$location', '$modal', function ($scope, $rootScope, $resource, $location, $modal) {  
	   
       	var init = function() {
       		
        	
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


