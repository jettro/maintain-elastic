'use strict';

var myApp = angular.module('myApp', ['ngRoute','myApp.directives.navbar']);

myApp.config(function($routeProvider) {
    $routeProvider.when('/dashboard', {templateUrl: '/assets/partials/dashboard.html', controller: 'DashboardCtrl'});
    $routeProvider.when('/indexes', {templateUrl: '/assets/partials/indexes.html', controller: 'IndexCtrl'});
    $routeProvider.otherwise({redirectTo: '/dashboard'});
});
