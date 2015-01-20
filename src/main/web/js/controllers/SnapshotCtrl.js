function SnapshotCtrl($scope, $modal, snapshotService, $rootScope) {
    $scope.repositories = [];
    $scope.snapshots = [];
    $scope.selectedRepository = "";

    $scope.$watch('selectedRepository', function () {
        $scope.listSnapshots();
    });

    $scope.initRepositories = function () {
        snapshotService.loadRepositories(function (data) {
            $scope.repositories = data;
        });
    };

    $scope.selectRepository = function(repository) {
        $scope.selectedRepository = repository.name;
    };

    $scope.listSnapshots = function() {

        if ($scope.selectedRepository !== "") {
            snapshotService.loadSnapshots($scope.selectedRepository, function (running,snapshots) {
                $scope.running = running;
                $scope.snapshots = snapshots;
            });
        }
    };


    function createNotification(message) {
        $rootScope.$broadcast('msg:notification', 'success', message);
    }
}
SnapshotCtrl.$inject = ['$scope', '$modal', 'snapshotService', '$rootScope'];