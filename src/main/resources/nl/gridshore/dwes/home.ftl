<#-- @ftlvariable name="" type="nl.gridshore.dwes.HomeView" -->
<!DOCTYPE html>
<html lang="en" ng-app="myApp">
<head>
    <meta charset="utf-8" />
    <title>DWAS</title>
    <link href="/assets/style/main.css" rel="stylesheet">

</head>
<body>
<header>
    <nav>
        <ul>
            <li><a href="#/dashboard">Dashboard</a></li>
            <li><a href="#/info">Info</a></li>
        </ul>
    </nav>
</header>
<p>We are connected to the cluster <strong>${clusterName?html}</strong></p>

<div ng-view>
</div>

<footer>
    <p>Copyright 2014 Jettro Coenradie</p>
</footer>
<script src="/assets/js/angular-1.2.16.min.js"></script>
<script src="/assets/js/angular-route-1.2.16.min.js"></script>
<script src="/assets/js/app.js"></script>
</body>
</html>