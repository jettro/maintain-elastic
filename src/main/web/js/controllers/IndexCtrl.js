/* Controllers */
function IndexCtrl($scope,$modal,indexService) {
    $scope.indexes = [];

    $scope.initIndexes = function () {
        indexService.loadIndices(function(data) {
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
            resolve: {index: function () {
                return angular.copy(index)
            } }};
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                var changedIndex = {"name":index.name,"numReplicas":result.numReplicas};
                indexService.changeIndex(changedIndex,function(changeIndexResult){
                    index.numberOfReplicas = result.numReplicas;
                });
            }
        }, function () {
            // Nothing to do here
        });
    };

    $scope.deleteIndex = function(index) {
        indexService.deleteIndex(index.name, function(data) {
            var i = $scope.indexes.indexOf(index);
            $scope.indexes.splice(i,1);
        })
    };

    $scope.closeIndex = function(index) {
        indexService.closeIndex(index.name, function(data) {
           index.state = "CLOSED";
        });
    };

    $scope.openIndex = function(index) {
        indexService.openIndex(index.name, function(data) {
            $scope.initIndexes();
        });

    };

    $scope.optimizeIndexDialog = function(index) {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/optimizeindex.html',
            controller: 'OptimizeIndexDialogCtrl',
            resolve: {index: function () {
                return angular.copy(index)
            } }};
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                var optimizedIndex = {"name":index.name,"maxSegments":result.maxSegments};
                indexService.optimizeIndex(index.name,result.maxSegments,function(optimizeIndexResult){
                    $scope.initIndexes();
                });
            }
        }, function () {
            // Nothing to do here
        });
    };

    $scope.copyIndexDialog = function(index) {
        var opts = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            templateUrl: 'assets/template/dialog/copyindex.html',
            controller: 'CopyIndexDialogCtrl',
            resolve: {index: function () {
                return angular.copy(index)
            } }};
        var modalInstance = $modal.open(opts);
        modalInstance.result.then(function (result) {
            if (result) {
                indexService.copyIndex(result, function(data) {
                    $scope.initIndexes();
                });
            }
        }, function () {
            // Nothing to do here
        });
    };
}
IndexCtrl.$inject = ['$scope','$modal','indexService'];