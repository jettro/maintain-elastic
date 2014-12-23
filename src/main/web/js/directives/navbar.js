'use strict';

angular.module('myApp.directives.navbar', []).
    directive('navbar', ['$location', function ($location) {
        return {
            restrict: 'E',
            transclude: true,
            scope: {heading: '@'},
            controller: 'NavbarCtrl',
            templateUrl: '/assets/template/navbar/navbar.html',
            replace: true,
            link: function ($scope, $element, $attrs, navbarCtrl) {
                navbarCtrl.init();
                $scope.$location = $location;
                $scope.$watch('$location.path()', function (locationPath) {
                    navbarCtrl.selectByUrl(locationPath)
                });
            }
        }
    }]).
    directive('navbaritem', [function () {
        return {
            require:'^navbar',
            restrict: 'E',
            templateUrl: '/assets/template/navbar/navbaritem.html',
            replace: true,
            scope:{"theLink":"@link","theTitle":"@title"},
            link: function ($scope, $element, $attrs, navbarCtrl) {
                $scope.item={"title": $attrs['title'], "link": $attrs['link'], "selected": false};
                navbarCtrl.addItem($scope.item);
            }
        }
    }]).
    directive('navbardropdownitem', [function () {
        return {
            require:'^navbar',
            restrict: 'E',
            scope:{"theLink":"@link","theTitle":"@title"},
            templateUrl: '/assets/template/navbar/navbardropdownitem.html',
            replace: true,
            link: function ($scope, $element, $attrs, navbarCtrl) {
            }
        }
    }]).
    directive('navbardropdown', [function () {
        return {
            require:'^navbar',
            restrict: 'E',
            transclude: true,
            scope:{"theTitle":"@title","theLink":"@link"},
            templateUrl: '/assets/template/navbar/navbardropdown.html',
            replace: true,
            link: function ($scope, $element, $attrs, navbarCtrl) {
                $scope.item={"title": $scope.theTitle, "link": $scope.theLink, "selected": false};
                navbarCtrl.addItem($scope.item);
            }
        }
    }]);

