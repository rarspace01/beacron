<?php

$servername = "mysql5-25.perso";
$username = "dcdnrshodcdndb";
$password = "S3cureSQL";
$dbname = "dcdnrshodcdndb";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if(!is_null($_POST['hwaddress'])){

$stm = "INSERT INTO beacron_users (hwaddress,lat,lng,acc,lastUpdate)
VALUES('".$_POST['hwaddress']."','".$_POST['lat']."','".$_POST['lng']."','".$_POST['acc']."',".time().")
ON DUPLICATE KEY UPDATE lat='".$_POST['lat']."',lng='".$_POST['lng']."',acc='".$_POST['acc']."',lastUpdate='".time()."';";

if ($conn->query($stm) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();


}

?>
