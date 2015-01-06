function OptimizeIndexDialogCtrl ($scope, $modalInstance, index) {
    $scope.index = index;
    $scope.optimizedIndex = {
        "maxSegments":index.numberOfSegments
    };

    $scope.close = function (result) {
        $modalInstance.close($scope.optimizedIndex);
    };

}
OptimizeIndexDialogCtrl.$inject = ['$scope', '$modalInstance','index'];