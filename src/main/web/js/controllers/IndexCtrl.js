/* Controllers */
function IndexCtrl($scope, $modal, indexService, $rootScope) {
    $scope.indexes = [];

    $scope.initIndexes = function () {
        indexService.loadIndices(function (data) {
            $scope.indexes = data;
        });
    };

    $scope.editIndexDialog = function (index) {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/editindex.html',
            controller: 'EditIndexDialogCtrl',
            resolve: {
                index: function () {
                    return angular.copy(index)
                }
            }
        };
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                var changedIndex = {"name": index.name, "numReplicas": result.numReplicas};
                indexService.changeIndex(changedIndex, function () {
                    index.numberOfReplicas = result.numReplicas;
                    createNotification("Changed number of replicas for index " + index.name);
                });
            }
        }, function () {
            // Nothing to do here
        });
    };

    $scope.deleteIndex = function (index) {
        indexService.deleteIndex(index.name, function () {
            var i = $scope.indexes.indexOf(index);
            $scope.indexes.splice(i, 1);
            createNotification("Deleted the index " + index.name);
        })
    };

    $scope.closeIndex = function (index) {
        indexService.closeIndex(index.name, function () {
            index.state = "CLOSED";
            createNotification("Closed the index " + index.name);
        });
    };

    $scope.openIndex = function (index) {
        indexService.openIndex(index.name, function () {
            $scope.initIndexes();
            createNotification("Opened the index " + index.name);
        });

    };

    $scope.optimizeIndexDialog = function (index) {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/optimizeindex.html',
            controller: 'OptimizeIndexDialogCtrl',
            resolve: {
                index: function () {
                    return angular.copy(index)
                }
            }
        };
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                indexService.optimizeIndex(index.name, result.maxSegments, function () {
                    $scope.initIndexes();
                    createNotification("Started optimizing the index " + index.name + ". Use refresh to monitor progress");
                });
            }
        }, function () {
            // Nothing to do here
        });
    };

    $scope.copyIndexDialog = function (index) {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/copyindex.html',
            controller: 'CopyIndexDialogCtrl',
            resolve: {
                index: function () {
                    return angular.copy(index)
                }
            }
        };
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                indexService.copyIndex(result, function () {
                    $scope.initIndexes();
                    createNotification("Copied into the index " + index.name);
                });
            }
        }, function () {
            // Nothing to do here
        });
    };

    $scope.createNewIndexDialog = function () {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/createnewindex.html',
            controller: 'CreateNewIndexDialogCtrl'
        };
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                indexService.copyIndex(result, function () {
                    $scope.initIndexes();
                    createNotification("Created the index " + result.name);
                });
            }
        }, function () {
            // Nothing to do here
        });
    };

    $scope.createAlias = function (index) {
        indexService.createAlias(index.name, function () {
            $scope.initIndexes();
            createNotification("Created alias for the index " + index.name);
        });
    };

    function createNotification(message) {
        $rootScope.$broadcast('msg:notification', 'success', message);
    }
}
IndexCtrl.$inject = ['$scope', '$modal', 'indexService', '$rootScope'];