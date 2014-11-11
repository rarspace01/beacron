<?php

$dbhandle = sqlite_open('users.db', 0666, $error);
if (!$dbhandle) die ($error);


$query = "SELECT hwaddress,lat,lng,acc,lastUpdate FROM beacron_users WHERE hwaddress ='".$_POST['host']."'";

$result = sqlite_query($dbhandle, $query);
if (!$result) die("Cannot execute query.");

while ($row = sqlite_fetch_array($result, SQLITE_ASSOC)) {
	echo "".$row['lat']."/".$row['lng'];
    //echo $row['hwaddress']." - ".$row['lat'].$row['lng'].$row['acc'];
    //echo "<br>";
}

sqlite_close($dbhandle);

?>
