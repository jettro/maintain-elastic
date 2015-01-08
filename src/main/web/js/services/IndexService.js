serviceModule.factory('indexService', ['$http','$filter', function ($http,$filter) {
    function IndexService($http,$filter) {

        this.loadIndices = function (callback) {
            $http.get('/index').success(function (data) {
                callback($filter('orderBy')(data, 'name'));
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

        this.createAlias = function(indexName,callback) {
            $http.post('/index/' + indexName + '/createalias').success(function(data) {
                callback("The alias is created");
            });
            // TODO Error handling
        };
    }

    return new IndexService($http,$filter);
}]);
