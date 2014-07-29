'use strict';


var app = angular.module('visitui', ['ngResource']);

app.controller('VisitController', ['$scope', '$resource', function($scope, $resource) {

  var visitResource = $resource('api/rest/visits');

  $scope.existingVisits = visitResource.get();

  $scope.newVisit = {
    "deleted":false,
    "collectedBy":{"deleted":false,"extId":"FWEK1D"},
    "extId":"VMBI2424",
    "visitLocation":{"deleted":false,"extId":"MBI000001"},
    "visitDate":1330405200001,
    "roundNumber":1,
    "extensions":[]};

  $scope.status="none";

  visitResource.save({}, $scope.newVisit,
    function (data) {$scope.status="yes"; $scope.response=data;},
    function (data) {$scope.status="no"; $scope.response=data;});

  $scope.updatedVisits = visitResource.get();

}]);
