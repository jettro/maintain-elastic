serviceModule.factory('indexService', ['$http',function ($http) {
    function IndexService($http) {

        this.loadIndices = function(callback) {
            $http.get('/index').success(function (data) {
                callback(data);
            });
            // TODO error handling
        };

        this.changeIndex = function(changedIndex, callback) {
            $http.post('/index/'+changedIndex.name,changedIndex).success(function (data) {
                callback("The index is changed");
            });
            // TODO error handling
        };
    }

    return new IndexService($http);
}]);
