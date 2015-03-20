<html>
<head> 
<link href="ap_style.css" rel="stylesheet">
</head>
<body>
<h1>Common Sense Size Comparison Results:</h1>
<div class='results'>
<?php 
$var1 = $_GET["arg1"];
$var2 = $_GET["arg2"];

if(($var1 != NULL) && ($var2 != NULL)) :
	$output = shell_exec("java -jar Query.jar \"$var1\" \"$var2\"");
	echo $output;
else :
	print("Sorry, size comparisons can only be made with 2 arguments.");
endif;
?><br>
</div>
</body>
</html>
