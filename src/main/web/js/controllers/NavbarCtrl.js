function NavbarCtrl($scope, $http, $interval) {
    var items = [];

    this.addItem = function (item) {
        items.push(item);
    };

    this.select = $scope.select = function (item) {
        angular.forEach(items, function (item) {
            item.selected = false;
        });
        item.selected = true;
    };

    this.selectByUrl = function (url) {
        angular.forEach(items, function (item) {
            if (item.link == url.split("/")[1]) {
                $scope.select(item);
            }
        });
    };

    this.init = function () {
        clusterStatus();
        $interval(function(){
            clusterStatus();
        },5000);
    };

    function clusterStatus() {
        $http.get('/cluster/status')
            .success(function (data) {
                $scope.clusterStatus = data;
            })
            .error(function (data) {
                $scope.clusterStatus = {"name":"unknown","status":"red"};
            });
    }
}
NavbarCtrl.$inject = ['$scope', '$http', '$interval'];
