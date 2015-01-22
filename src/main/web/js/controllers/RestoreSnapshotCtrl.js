function RestoreSnapshotCtrl ($scope, $modalInstance, snapshot) {
    $scope.snapshot = snapshot;
    $scope.dialog = {};

    $scope.close = function (result) {
        result.snapshot = $scope.snapshot.name;
        result.repository = $scope.snapshot.repository;
        $modalInstance.close(result);
    };

}
RestoreSnapshotCtrl.$inject = ['$scope', '$modalInstance','snapshot'];
