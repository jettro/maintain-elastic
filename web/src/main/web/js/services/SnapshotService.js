serviceModule.factory('snapshotService', ['$http','$filter','$log','$rootScope', function ($http,$filter,$log,$rootScope) {
    function SnapshotService($http,$filter,$log) {

        this.loadRepositories = function (callback) {
            $http.get('/repository').success(function (data) {
                callback($filter('orderBy')(data, 'name'));
            }).error(httpError);
        };

        this.loadSnapshots = function(repository, callback) {
            $http.get('/repository/'+repository+'/snapshots').success(function (data) {
                if (data.runningSnapshot) {
                    callback(true,$filter('orderBy')(data.snapshotStatussus, 'repository'));
                } else {
                    callback(false,$filter('orderBy')(data.snapshots, 'name'));
                }
            }).error(httpError);
        };

        this.deleteRepository = function(repository, callback) {
            $http.delete('/repository/'+repository).success(function(data){
                callback();
            }).error(httpError);
        };

        this.createRepository = function(newrepository, callback) {
            $http.post('/repository',newrepository).success(function(data){
                callback();
            }).error(httpError);
        };

        this.removeSnapshot = function(repository,snapshot,callback) {
            $http.delete('/repository/'+repository+'/snapshot/'+snapshot).success(function(data){
                callback();
            }).error(httpError);
        };

        this.createSnapshot = function(newSnapshot,callback) {
            $http.post('/repository/'+newSnapshot.repository+'/snapshot',newSnapshot).success(function(data) {
                callback();
            }).error(httpError);
        };

        this.restoreSnapshot = function(restoreRequest,callback) {
            $http.post('/repository/'+restoreRequest.repository+'/snapshot/'+restoreRequest.snapshot,restoreRequest)
                .success(function(data) {
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

    return new SnapshotService($http,$filter,$log,$rootScope);
}]);

