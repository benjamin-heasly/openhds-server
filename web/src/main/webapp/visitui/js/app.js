'use strict';


var app = angular.module('visitui', ['ngResource', 'ngRoute']);

app.controller('VisitController', ['$scope', '$resource', '$location', function($scope, $resource, $location) {

  var visitResource = $resource('api/rest/visits');
  $scope.allVisits = visitResource.get();

  $scope.status = "Welcome.";

  var date = new Date();
  $scope.newForm = {visitDate:date.getTime(), roundNumber:1};

  $scope.viewList = function() {
    $scope.allVisits = visitResource.get();
    $location.path("/list");
  };

  $scope.newVisit = function() {
    $location.path("/new");
  };

  $scope.handleSuccess = function (response) {
    $scope.status="Saved.";
    $location.path("/list");
    $scope.allVisits = visitResource.get();
  }

  $scope.handleError = function (response) {
    if (response.data instanceof Object) {
      $scope.status = "Error: " + response.data.errors.toString();
    } else {
      $scope.status = "Error.";
    }
  }

  $scope.saveVisit = function() {
    $scope.status="Waiting for response.";
    visitResource.save({}, $scope.newForm, $scope.handleSuccess, $scope.handleError);
  };

}]);

app.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/list', {
        templateUrl: 'visitui/partials/list.html',
        controller: 'VisitController'
      }).
      when('/new', {
        templateUrl: 'visitui/partials/new.html',
        controller: 'VisitController'
      }).
      otherwise({
        redirectTo: '/list'
      });
  }]);

