<?php
$files = glob('C:/xampp/htdocs/demo/uploads/*'); // get all file names
foreach($files as $file){ // iterate files
  if(is_file($file))
    unlink($file); // delete file
}


?>