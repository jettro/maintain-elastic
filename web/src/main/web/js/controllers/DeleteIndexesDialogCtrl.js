function DeleteIndexesDialogCtrl ($scope, $modalInstance) {
    $scope.close = function () {
        $modalInstance.close($scope.indexes);
    };

}
DeleteIndexesDialogCtrl.$inject = ['$scope', '$modalInstance'];