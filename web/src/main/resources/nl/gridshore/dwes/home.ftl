<#-- @ftlvariable name="" type="nl.gridshore.dwes.HomeView" -->
<!DOCTYPE html>
<html lang="en" ng-app="myApp">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>DWAS</title>
    <link href="/assets/style/app.min.css" rel="stylesheet">

</head>
<body>
<navbar heading="Elastic tools">
    <navbaritem link="dashboard" title="Dashboard"></navbaritem>
    <navbaritem link="indexes" title="Index"></navbaritem>
    <navbaritem link="snapshots" title="Snapshot"></navbaritem>
    <navbaritem link="about" title="About"></navbaritem>
</navbar>
<div class="container-fluid" ng-controller="NotificationCtrl">
    <alert ng-repeat="(key,value) in alerts" type="{{value.type}}">{{value.message}}</alert>
</div>
<div class="container-fluid" ng-view>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <footer>
                <p>Copyright 2014 Jettro Coenradie</p>
            </footer>
        </div>
    </div>
</div>
<script src="/assets/js/dwes.js"></script>
</body>
</html>