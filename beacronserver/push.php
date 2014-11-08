<html>
<header>
<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css" />
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
</header>
<body>
<div id="map" style="height: 90%"}></div>
<script>
<?php

$dbhandle = sqlite_open('users.db', 0666, $error);
if (!$dbhandle) die ($error);

//try{

//$stm = "CREATE TABLE 'beacron_users' (
	//'hwaddress'	TEXT NOT NULL PRIMARY KEY,
	//'lat'	NUMERIC,
	//'lng'	NUMERIC,
	//'acc'	NUMERIC,
	//'lastUpdate'	NUMERIC
//);";
//$ok = sqlite_exec($dbhandle, $stm);
//}
//catch (Exception $e) {
    //echo 'Exception abgefangen: ',  $e->getMessage(), "\n";
//}

if(!is_null($_POST['hwaddress'])){

$stm1 = "INSERT OR REPLACE INTO beacron_users ('hwaddress','lat','lng','acc','lastUpdate') VALUES('".$_POST['hwaddress']."','".$_POST['lat']."','".$_POST['lng']."','".$_POST['acc']."',".time().")";

$ok1 = sqlite_exec($dbhandle, $stm1);
if (!$ok1) die("Cannot execute statement.");


echo "Inserted location successfully";


}

$query = "SELECT hwaddress,lat,lng,acc,lastUpdate FROM beacron_users";

$result = sqlite_query($dbhandle, $query);
if (!$result) die("Cannot execute query.");

while ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
	echo "var hwadress=\"".$row['hwaddress']."\";";
	echo "var lat=\"".$row['lat']."\";";
	echo "var lng=\"".$row['lng']."\";";
	echo "var acc=\"".$row['acc']."\";";
    //echo $row['hwaddress']." - ".$row['lat'].$row['lng'].$row['acc'];
    //echo "<br>";
}

sqlite_close($dbhandle);

?>	
var map = L.map('map').setView([lat, lng], 16);
L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18
}).addTo(map);
var circle = L.circle([lat, lng], acc, {
    color: 'red',
    fillColor: '#f03',
    fillOpacity: 0.5
}).bindPopup("acc: "+acc).addTo(map);
</script>
</body>
</html>


