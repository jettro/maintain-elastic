/* Controllers */
function IndexCtrl($scope,$http) {
    $scope.indexes = [];

    $scope.initIndexes = function () {
        $http.get('/indexes').success(function (data) {
            $scope.indexes = data;
        });
    };

}
IndexCtrl.$inject = ['$scope','$http'];