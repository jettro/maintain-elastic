'use strict';

var myApp = angular.module('myApp', ['ngRoute']);
//var myApp = angular.module('myApp', []);

myApp.controller('IndexCtrl', function ($scope, $http) {
    $scope.indexes = [];

    $scope.initIndexes = function () {
        $http.get('/indexes').success(function (data) {
            $scope.indexes = data;
        });
    };
});

myApp.config(function($routeProvider) {
    $routeProvider.when('/dashboard', {templateUrl: '/assets/partials/dashboard.html', controller: 'IndexCtrl'});
    $routeProvider.otherwise({redirectTo: '/dashboard'});
});
