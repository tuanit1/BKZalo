<?php
    require "connection.php";

    if(!array_key_exists("data", $_POST)){
        echo "Đây là phương thức POST, không thể gọi được như thế này!";
        die();
    }

    $post_data = ($_POST['data']);

    $postObj = json_decode($post_data, true);

    //Tuan make it complicated!
    if($postObj['method_name'] == 'method_get_chat_list'){
        
        $uid = $postObj['uid'];

        $query = "SELECT * FROM tbl_user WHERE `id` != '$uid'";

        $query_result = mysqli_query($connect, $query);

        $jsObjAll = array();
        $jsObj_user = array();

        while($row = mysqli_fetch_assoc($query_result)){
            $data['id'] = $row['id'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['birthday'] = $row['birthday'];
            $data['phone'] = $row['phone'];
            $data['password'] = $row['password'];
            $data['bio'] = $row['bio'];

            array_push($jsObj_user, $data);
        } 

        $jsObjAll['array_user'] = $jsObj_user;

        echo json_encode($jsObjAll);

        die();
    }
?>