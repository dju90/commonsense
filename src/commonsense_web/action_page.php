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
print("First Argument Passed= $var1<br>");  
print("Second Argument Passed= $var2<br>");

$output = shell_exec("java -jar Test2.jar");
echo $output;

echo "<html><body>something</body></html>"
?><br>
</div>
</body>
</html>