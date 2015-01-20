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

    $scope.deleteRepository = function(repository) {
        snapshotService.deleteRepository(repository.name, function() {
            if ($scope.selectedRepository === name) {
                $scope.selectedRepository = "";
            }
            $scope.initRepositories();
        });
    };

    $scope.listSnapshots = function() {

        if ($scope.selectedRepository !== "") {
            snapshotService.loadSnapshots($scope.selectedRepository, function (running,snapshots) {
                $scope.running = running;
                $scope.snapshots = snapshots;
            });
        }
    };

    $scope.createNewRepositoryDialog = function () {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/createsnapshotrepository.html',
            controller: 'CreateSnapshotRepositoryCtrl'
        };
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                snapshotService.createRepository(result, function() {
                    $scope.initRepositories();
                    $scope.selectedRepository = "";
                });
            }
        }, function () {
            // Nothing to do here
        });
    };


    function createNotification(message) {
        $rootScope.$broadcast('msg:notification', 'success', message);
    }
}
SnapshotCtrl.$inject = ['$scope', '$modal', 'snapshotService', '$rootScope'];