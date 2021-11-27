<?php
    require "connection.php";

    if(!array_key_exists("data", $_POST)){
        echo "Đây là phương thức POST, không thể gọi được như thế này!";
        die();
    }

    $post_data = ($_POST['data']);

    $postObj = json_decode($post_data, true);

    //Tuan
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
            $data['bio'] = $row['bio'];
            $data['email'] = $row['email'];
            $data['isOnline'] = $row['isOnline'];

            array_push($jsObj_user, $data);
        } 

        $jsObjAll['array_user'] = $jsObj_user;

        echo json_encode($jsObjAll);

        die();
    }

    //Nhi
    if($postObj['method_name'] == 'method_get_phonebook_list'){
        
        $uid = $postObj['uid'];

        $query = "SELECT DISTINCT id, name, image, birthday, phone, bio, email  FROM `tbl_relationship` INNER JOIN `tbl_user` 
                    ON tbl_relationship.user_id1=tbl_user.id OR tbl_relationship.user_id2=tbl_user.id 
                    WHERE tbl_relationship.status='friend' 
                    AND (user_id1='$uid' OR user_id2='$uid') 
                    AND (NOT tbl_user.id='$uid')";

        $query_result = mysqli_query($connect, $query);

        $jsObjAll = array();
        $jsObj_user = array();

        while($row = mysqli_fetch_assoc($query_result)){
            $data['id'] = $row['id'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['birthday'] = $row['birthday'];
            $data['phone'] = $row['phone'];
            $data['bio'] = $row['bio'];
            $data['email'] = $row['email'];
            $data['isOnline'] = $row['isOnline'];

            array_push($jsObj_user, $data);
        } 

        $jsObjAll['array_user'] = $jsObj_user;

        echo json_encode($jsObjAll);

        die();
    }

    //Nhi
    if($postObj['method_name'] == 'method_get_user'){
        
        $email = $postObj['email'];

        $query = "SELECT * FROM tbl_user WHERE `email` = '$email'";

        $query_result = mysqli_query($connect, $query);

        $jsObjAll = array();
        $jsObj_user = array();

        while($row = mysqli_fetch_assoc($query_result)){
            $data['id'] = $row['id'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['birthday'] = $row['birthday'];
            $data['phone'] = $row['phone'];
            $data['bio'] = $row['bio'];
            $data['email'] = $row['email'];
            $data['isOnline'] = $row['isOnline'];

            array_push($jsObj_user, $data);
        } 

        $jsObjAll['array_user'] = $jsObj_user;

        echo json_encode($jsObjAll);

        die();
    }

    //Nhi
    if ($postObj['method_name']== 'method_signup')
    {
        $name = $postObj['name'];
        $email = $postObj['email'];
        $phone = $postObj['phone'];

        $query = "INSERT INTO `tbl_user` (`id`, `name`, `image`, `birthday`, `phone`, `bio`, `email`, `isOnline`) VALUES (NULL, '$name', NULL, NULL, '$phone', NULL, '$email', NULL)";

        $query_result = mysqli_query($connect, $query);

        if ($query_result)
        {
            echo "success";
        }
        else
        {
            echo "Failed";
        }
        mysqli_close($connect);
    }

    //Nhi
    if ($postObj['method_name']== 'method_delete_phonebook')
    {
        $uid1 = $postObj['uid1'];
        $uid2 = $postObj['uid2'];

        $query = "UPDATE `tbl_relationship` SET status='' WHERE (user_id1=$uid1 AND user_id2=$uid2) OR (user_id2=$uid1 AND user_id1=$uid2)";

        $query_result = mysqli_query($connect, $query);

        if ($query_result)
        {
            echo "success";
        }
        else
        {
            echo "Failed";
        }
        mysqli_close($connect);
    }

    //Nhi
    if ($postObj['method_name']== 'method_update_info')
    {
        $birthday = $postObj['birthday'];
        $image = $postObj['image'];
        $uid = $postObj['uid'];

        $query = "UPDATE `tbl_user` SET `image`='$image', `birthday`='$birthday' WHERE `id`='$uid'";

        $query_result = mysqli_query($connect, $query);

        if ($query_result)
        {
            echo "success";
        }
        else
        {
            echo "Failed";
        }
        mysqli_close($connect);
    }

    //Nhi
    if ($postObj['method_name']== 'method_block_phonebook')
    {
        $uid1 = $postObj['uid1'];
        $uid2 = $postObj['uid2'];

        $query = "UPDATE `tbl_relationship` SET `blocker` = '$uid1', `status` = 'block' WHERE (`user_id1` = $uid1 AND `user_id2` = $uid2) OR (`user_id1` = $uid2 AND `user_id2` = $uid1)";


        $query_result = mysqli_query($connect, $query);

        if ($query_result)
        {
            echo "success";
        }
        else
        {
            echo "Failed";
        }
        mysqli_close($connect);
    }
?>