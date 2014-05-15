'use strict';

var myApp = angular.module('myApp', []);

myApp.controller('IndexCtrl', function ($scope, $http) {
    $scope.indexes = [];

    $scope.initIndexes = function () {
        $http.get('/indexes').success(function (data) {
            $scope.indexes = data;
        });
    };
});