'use strict';


// Declare app level module which depends on filters, and services
angular.module('tabletuing', [
  'ngRoute',
  'tabletuing.filters',
  'tabletuing.services',
  'tabletuing.directives',
  'tabletuing.controllers'
  ]).

config(['$routeProvider', function($routeProvider) {
  $routeProvider
  .when('/form/location', {
  		templateUrl: 'partials/form/location.html', 
  		controller: 'LocationCtrl'
  	})
  .when('/form/individual', {
  		templateUrl: 'partials/form/individual.html', 
  		controller: 'IndividualCtrl'
  	})
  .when('/form/household', {
  		templateUrl: 'partials/form/household.html', 
  		controller: 'HouseholdCtrl'
  	})
  .when('/form/marriage', {
  		templateUrl: 'partials/form/marriage.html', 
  		controller: 'MarriageCtrl'
  	})
  .when('/home', {templateUrl: 'partials/home.html'})
  .otherwise({redirectTo: '/home'});
}]);


