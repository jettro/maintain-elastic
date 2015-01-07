serviceModule.factory('indexService', ['$http', function ($http) {
    function IndexService($http) {

        this.loadIndices = function (callback) {
            $http.get('/index').success(function (data) {
                callback(data);
            });
            // TODO error handling
        };

        this.changeIndex = function (changedIndex, callback) {
            $http.post('/index/' + changedIndex.name, changedIndex).success(function (data) {
                callback("The index is changed");
            });
            // TODO error handling
        };

        this.deleteIndex = function (indexName, callback) {
            $http.delete('/index/' + indexName).success(function (data) {
                callback("The index is removed");
            });
            // TODO error handling
        };

        this.closeIndex = function (indexName, callback) {
            $http.post('/index/' + indexName + '/close').success(function (data) {
                callback("The index is closed");
            });
            // TODO error handling
        };

        this.openIndex = function (indexName, callback) {
            $http.post('/index/' + indexName + '/open').success(function (data) {
                callback("The index is opened");
            });
            // TODO error handling
        };

        this.optimizeIndex = function (indexName, maxSegments, callback) {
            $http.post('/index/' + indexName + '/optimize?max=' + maxSegments).success(function (data) {
                callback("The index optimization is started");
            });
            // TODO error handling
        };

        this.copyIndex = function (copyTo,callback) {
            $http.post('/index/copy', copyTo).success(function (data) {
                callback("The index is copied");
            });
            // TODO error handling
        };
    }

    return new IndexService($http);
}]);
