'use strict';

var myApp = angular.module('myApp', ['ngRoute']);

myApp.controller('IndexCtrl', function ($scope, $http) {
    $scope.indexes = [];

    $scope.initIndexes = function () {
        $http.get('/indexes').success(function (data) {
            $scope.indexes = data;
        });
    };
});

myApp.controller('InfoCtrl', function ($scope, $http) {

});

myApp.config(function($routeProvider) {
    $routeProvider.when('/dashboard', {templateUrl: '/assets/partials/dashboard.html', controller: 'IndexCtrl'});
    $routeProvider.when('/info', {templateUrl: '/assets/partials/info.html', controller: 'InfoCtrl'});
    $routeProvider.otherwise({redirectTo: '/dashboard'});
});
