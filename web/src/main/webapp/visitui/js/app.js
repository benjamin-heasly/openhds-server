'use strict';


var app = angular.module('visitui', ['ngResource']);

app.controller('VisitController', ['$scope', '$resource', function($scope, $resource) {
  var visitResource = $resource('api/rest/visits');
  $scope.visitMess = visitResource.get();
}]);
