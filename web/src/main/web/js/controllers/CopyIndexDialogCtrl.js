function CopyIndexDialogCtrl ($scope, $modalInstance, FileUploader, index) {
    var uploader = $scope.uploader = new FileUploader({url: '/index/settings'});

    uploader.onSuccessItem = function(fileItem, response, status, headers) {
        console.info('onSuccessItem', fileItem, response, status, headers);
        var reSettings = /^settings\.json.*/;
        var reMapping = /(\w+)-mapping\.json.*/;
        var settings = reSettings.exec(response);
        var mapping = reMapping.exec(response);
        if (settings) {
            $scope.copyTo.settings = response;
        } else if (mapping) {
            if (!$scope.copyTo.mappings) {
                $scope.copyTo.mappings = {};
            }
            $scope.copyTo.mappings[mapping[1]]=mapping[0];
        } else {
            console.log("Unrecognized file type, require ....")
        }
        console.log($scope.copyTo);
    };
    uploader.onErrorItem = function(fileItem, response, status, headers) {
        // TODO error handling
        console.info('onErrorItem', fileItem, response, status, headers);
    };
    uploader.onCancelItem = function(fileItem, response, status, headers) {
        // TODO remove from queue
        console.info('onCancelItem', fileItem, response, status, headers);
    };

    $scope.index = index;
    $scope.copyTo = {
        "copyFrom":index.name
    };

    $scope.close = function (result) {
        $modalInstance.close($scope.copyTo);
    };

}
CopyIndexDialogCtrl.$inject = ['$scope', '$modalInstance','FileUploader','index'];