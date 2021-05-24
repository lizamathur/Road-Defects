<?php
	$user = "root";
	$pass = "";
	$host = "localhost";
	$db = "roadalert";

	$con = mysqli_connect($host, $user, $pass, $db);

	
	if($con){

		$image = $_POST["image"];
		$name = $_POST["name"];
		$upload_path = "uploads/$name";

		
		file_put_contents($upload_path, base64_decode($image));
		echo "Successful";

	}else{
		echo "not Successful";
	}
	mysqli_close($con);
?>