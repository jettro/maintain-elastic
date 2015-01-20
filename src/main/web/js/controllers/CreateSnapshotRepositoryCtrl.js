function CreateSnapshotRepositoryCtrl ($scope, $modalInstance) {
    $scope.dialog = {"type":"fs"};

    $scope.close = function (result) {
        $modalInstance.close(result);
    };

}
CreateSnapshotRepositoryCtrl.$inject = ['$scope', '$modalInstance'];
