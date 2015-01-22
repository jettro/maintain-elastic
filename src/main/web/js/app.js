'use strict';

var myApp = angular.module('myApp', ['ngRoute','myApp.services','myApp.directives.navbar','myApp.directives.confirm','ui.bootstrap','angularFileUpload']);

myApp.config(['$routeProvider',function($routeProvider) {
    $routeProvider.when('/dashboard', {templateUrl: '/assets/partials/dashboard.html', controller: 'DashboardCtrl'});
    $routeProvider.when('/indexes', {
        templateUrl: '/assets/partials/indexes.html',
        controller: 'IndexCtrl'
    });
    $routeProvider.when('/snapshots', {
        templateUrl: '/assets/partials/snapshots.html',
        controller: 'SnapshotCtrl'
    });
    $routeProvider.when('/about', {
        templateUrl: '/assets/partials/about.html'
    });
    $routeProvider.otherwise({redirectTo: '/dashboard'});
}]);

myApp.factory('$exceptionHandler',['$injector','$log', function($injector,$log) {
    return function(exception, cause) {
        $log.error(exception);
        var errorHandling = $injector.get('errorHandling');
        errorHandling.add(exception.message);
        throw exception;
    };
}]);

var serviceModule = angular.module('myApp.services', []);