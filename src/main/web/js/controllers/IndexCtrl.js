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

}
IndexCtrl.$inject = ['$scope','$modal','indexService'];