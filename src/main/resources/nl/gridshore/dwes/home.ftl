<#-- @ftlvariable name="" type="nl.gridshore.dwes.HomeView" -->
<html ng-app="myApp">
<head>
    <title>DWAS</title>
</head>
<body>
<p>Underneath a list of indexes in the cluster <strong>${clusterName?html}</strong></p>

<div ng-view>
</div>

<script src="/assets/js/angular-1.2.16.min.js"></script>
<script src="/assets/js/angular-route-1.2.16.min.js"></script>
<script src="/assets/js/app.js"></script>
</body>
</html>