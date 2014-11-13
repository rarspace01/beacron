<?php

$servername = "mysql5-25.perso";
$username = "dcdnrshodcdndb";
$password = "S3cureSQL";
$dbname = "dcdnrshodcdndb";


if($_POST['host'] != null){

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$query = "SELECT hwaddress,lat,lng,acc,lastUpdate FROM beacron_users WHERE hwaddress ='".$_POST['host']."'";

$result = $conn->query($query);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "".$row['lat']."/".$row['lng'];
    }
} else {
    
}
$conn->close();

}else
{
	
?>
<html>
<header>
<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css" />
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
</header>
<body>
<div id="map" style="height: 90%"}></div>
<script>
var map = L.map('map').setView([51, 9], 6);
L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18
}).addTo(map);
<?php	

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$query = "SELECT hwaddress,lat,lng,acc,lastUpdate FROM beacron_users";

$result = $conn->query($query);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
	
	echo "L.marker([".$row['lat'].", ".$row['lng']."]).addTo(map);";	
    echo "L.circle([".$row['lat'].", ".$row['lng']."], ".$row['acc'].", {
			color: 'red',
			fillColor: '#f03',
			fillOpacity: 0.5
		}).addTo(map).bindPopup('User: ".$row['hwaddress']." - ACC: ".$row['acc']."');";
    }
} else {
    
}
$conn->close();

?>	
</script>
</body>
</html>
<?php
}
?>
