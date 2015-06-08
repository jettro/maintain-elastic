serviceModule.factory('indexService', ['$http','$filter','$log','$rootScope', function ($http,$filter,$log,$rootScope) {
    function IndexService($http,$filter,$log) {

        this.loadIndices = function (callback) {
            $http.get('/index').success(function (data) {
                callback($filter('orderBy')(data, 'name'));
            }).error(httpError);
        };

        this.changeIndex = function (changedIndex, callback) {
            $http.post('/index/' + changedIndex.name, changedIndex).success(function (data) {
                $log.info("changed the index " + changedIndex.name);
                callback();
            }).error(httpError);
        };

        this.deleteIndex = function (indexName, callback) {
            $http.delete('/index/' + indexName).success(function (data) {
                $log.info("deleted the index " + indexName);
                callback();
            }).error(httpError);
        };

        this.closeIndex = function (indexName, callback) {
            $http.post('/index/' + indexName + '/close').success(function (data) {
                $log.info("closed the index " + indexName);
                callback();
            }).error(httpError);
        };

        this.openIndex = function (indexName, callback) {
            $http.post('/index/' + indexName + '/open').success(function (data) {
                $log.info("opened the index " + indexName);
                callback();
            }).error(httpError);
        };

        this.optimizeIndex = function (indexName, maxSegments, callback) {
            $http.post('/index/' + indexName + '/optimize?max=' + maxSegments).success(function (data) {
                $log.info("optimized the index " + indexName);
                callback();
            }).error(httpError);
        };

        this.copyIndex = function (copyTo,callback) {
            $http.post('/index/copy', copyTo).success(function (data) {
                $log.info("copied to the index " + copyTo.name);
                callback();
            }).error(httpError);
        };

        this.createAlias = function(indexName,callback) {
            $http.post('/index/' + indexName + '/createalias').success(function(data) {
                $log.info("alias created for the index " + indexName);
                callback();
            }).error(httpError);
        };

        var httpError = function (data) {
            var message;
            if (data.errors && data.errors.length > 0) {
                message = data.errors[0]
            } else {
                message = "Error without a message was thrown";
            }
            $log.error(message);
            $rootScope.$broadcast('msg:notification', 'danger', message);
        }
    }

    return new IndexService($http,$filter,$log,$rootScope);
}]);
