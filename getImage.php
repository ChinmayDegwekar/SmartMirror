<?php
 
 if($_SERVER['REQUEST_METHOD']=='GET'){
 
 
 
 header('content-type: image/jpeg');
 
 echo base64_decode($result['image']);
 

 
 }else{
 echo "Error";
 }
 ?>