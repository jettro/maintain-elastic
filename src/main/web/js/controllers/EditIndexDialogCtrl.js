function EditIndexDialogCtrl ($scope, $modalInstance, index) {
    $scope.index = index;
    $scope.changedIndex = {
        "numReplicas":index.numberOfReplicas
    };

    $scope.close = function (result) {
        $modalInstance.close($scope.changedIndex);
    };

}
EditIndexDialogCtrl.$inject = ['$scope', '$modalInstance','index'];