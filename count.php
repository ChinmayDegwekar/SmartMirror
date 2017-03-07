
<?php


$fi = new FilesystemIterator("C:/xampp/htdocs/demo/uploads", FilesystemIterator::SKIP_DOTS);
$count= iterator_count($fi);
echo $count;

?>