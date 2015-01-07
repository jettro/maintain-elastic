'use strict';

var myApp = angular.module('myApp', ['ngRoute','myApp.services','myApp.directives.navbar','myApp.directives.confirm','ui.bootstrap','angularFileUpload']);

myApp.config(['$routeProvider',function($routeProvider) {
    $routeProvider.when('/dashboard', {templateUrl: '/assets/partials/dashboard.html', controller: 'DashboardCtrl'});
    $routeProvider.when('/indexes', {templateUrl: '/assets/partials/indexes.html', controller: 'IndexCtrl'});
    $routeProvider.otherwise({redirectTo: '/dashboard'});
}]);

var serviceModule = angular.module('myApp.services', []);