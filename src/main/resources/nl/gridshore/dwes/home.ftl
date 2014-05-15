<#-- @ftlvariable name="" type="nl.gridshore.dwes.HomeView" -->
<html ng-app="myApp">
<head>
    <title>DWAS</title>
</head>
<body ng-controller="IndexCtrl">
<p>Underneath a list of indexes in the cluster <strong>${clusterName?html}</strong></p>

<div ng-init="initIndexes()">
    <ul>
        <li ng-repeat="index in indexes">{{index.name}}</li>
    </ul>
</div>

<script src="/assets/js/angular-1.2.16.min.js"></script>
<script src="/assets/js/app.js"></script>
</body>
</html>