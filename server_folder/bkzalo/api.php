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

        $query = "SELECT * FROM tbl_participant WHERE `user_id` = '$uid'";

        $query_result = mysqli_query($connect, $query);

        $jsObjAll = array();
        $jsObj_participant = array();
        $jsObj_user = array();
        $jsObj_room = array();
        $jsObj_message = array();

        //Participant
        while($row = mysqli_fetch_assoc($query_result)){

            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['nickname'] = $row['nickname'];
            $data['isAdmin'] = $row['isAdmin'];
            $data['isHide'] = $row['isHide'];
            $data['timestamp'] = $row['timestamp'];

            array_push($jsObj_participant, $data);

            $room_id = $row['room_id'];

            //Latest Message
            $query_latest_msg = "SELECT DISTINCT z.* FROM
            (
            SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
                    INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
                    INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
                    INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id AND tbl_participant.room_id = tbl_room.id)
                    WHERE tbl_room.id = $room_id AND tbl_message.type != 'leave'
            UNION

            SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, '' as nickname FROM tbl_message
                    INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
                    INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
                    WHERE tbl_message.type = 'leave' AND tbl_room.id = $room_id
            ) as z
            WHERE z.time > (SELECT tbl_participant.timestamp FROM tbl_participant WHERE tbl_participant.user_id = $uid AND tbl_participant.room_id = $room_id)
            ORDER BY z.time DESC LIMIT 1";
            $query_latest_msg_rs = mysqli_query($connect, $query_latest_msg);

            while($row = mysqli_fetch_assoc($query_latest_msg_rs)){
                $data9['id'] = $row['id'];
                $data9['user_id'] = $row['user_id'];
                $data9['room_id'] = $row['room_id'];
                $data9['type'] = $row['type'];
                $data9['message'] = $row['message'];
                $data9['time'] = $row['time'];
                $data9['isRemove'] = $row['isRemove'];
                $data9['isSeen'] = $row['isSeen'];
                $data9['name'] = $row['name'];
                $data9['image'] = $row['image'];
                $data9['nickname'] = $row['nickname'];

                array_push($jsObj_message, $data9);
            }

            //Room
            $query_room = "SELECT * FROM tbl_room WHERE `id` = $room_id";

            $query_room_rs = mysqli_query($connect, $query_room);

            while($row = mysqli_fetch_assoc($query_room_rs)){
                $data1['id'] = $row['id'];
                $data1['name'] = $row['name'];
                $data1['image'] = $row['image'];
                $data1['background'] = $row['background'];
                $data1['type'] = $row['type'];

                array_push($jsObj_room, $data1);

                //User (Room type: PRIVATE)
                if($row['type'] == 'private'){
                    $query_ptcp = "SELECT * FROM tbl_participant WHERE `room_id` = " .$row['id']. " AND `user_id` != $uid";

                    $query_ptcp_rs = mysqli_query($connect, $query_ptcp);
                    while($row = mysqli_fetch_assoc($query_ptcp_rs)){

                        $data3['user_id'] = $row['user_id'];
                        $data3['room_id'] = $row['room_id'];
                        $data3['nickname'] = $row['nickname'];
                        $data3['isAdmin'] = $row['isAdmin'];
                        $data3['isHide'] = $row['isHide'];
                        $data3['timestamp'] = $row['timestamp'];

                        array_push($jsObj_participant, $data3);

                        $query_user = "SELECT * FROM tbl_user WHERE `id` = " .$row['user_id'];
                        $query_user_rs = mysqli_query($connect, $query_user);

                        while($row = mysqli_fetch_assoc($query_user_rs)){
                            $data2['id'] = $row['id'];
                            $data2['name'] = $row['name'];
                            $data2['image'] = $row['image'];
                            $data2['birthday'] = $row['birthday'];
                            $data2['phone'] = $row['phone'];
                            $data2['email'] = $row['email'];
                            $data2['bio'] = $row['bio'];
                            $data2['isOnline'] = $row['isOnline'];

                            array_push($jsObj_user, $data2);
                        }
                    }
                }
            }
        }

        $jsObjAll['array_participant'] = $jsObj_participant;
        $jsObjAll['array_room'] = $jsObj_room;
        $jsObjAll['array_user'] = $jsObj_user;
        $jsObjAll['array_message'] = $jsObj_message;

        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_get_list_friend'){

        $uid = $postObj['uid'];

        $array_friend = array();

        $query = "SELECT tbl_user.* FROM `tbl_relationship`
                INNER JOIN tbl_user ON (tbl_user.id = user_id1 OR tbl_user.id = user_id2)
                WHERE (tbl_user.id != $uid AND tbl_relationship.status = 'friend' AND (tbl_relationship.user_id1 = $uid OR tbl_relationship.user_id2 = $uid))";

        $query_rs = mysqli_query($connect, $query);

        while($row = mysqli_fetch_assoc($query_rs)){
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
            $data['email'] = $row['email'];
            $data['bio'] = $row['bio'];
            $data['isOnline'] = $row['isOnline'];

            array_push($array_friend, $data);
        }

        $jsObjAll = array();
        $jsObjAll['array_user'] = $array_friend;

        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_add_group'){

        $isImageChange = $postObj['is_change_image'];
        $admin_id = $postObj['admin_id'];
        $json_member_id = $postObj['json_member_id'];
        $group_name = $postObj['group_name'];

        $query_create_group = "";

        if($isImageChange == 'false'){

            $query_create_group = "INSERT INTO `tbl_room` (`id`, `name`, `image`, `background`, `type`) VALUES (NULL, '$group_name', '', '', 'group')";

        }else{
            $room_image = rand(0, 99999) . "_" . $_FILES['image']['name'];

            $image_path = 'image/image_room/' . $room_image;

            if(!move_uploaded_file($_FILES['image']['tmp_name'], $image_path)){
                echo "fail upload";
                die();
            }

            $query_create_group = "INSERT INTO `tbl_room` (`id`, `name`, `image`, `background`, `type`) VALUES (NULL, '$group_name', '$room_image', '', 'group')";

        }

        if(mysqli_query($connect, $query_create_group)){
            $room_id = mysqli_insert_id($connect);

            $query_admin = "INSERT INTO `tbl_participant` (`user_id`, `room_id`, `nickname`, `isAdmin`, `isHide`, `timestamp`) VALUES ('$admin_id', '$room_id', '', '1', '0', current_timestamp)";

            if(!mysqli_query($connect, $query_admin)){
                echo "Fail execute query: " .$query;
                die();
            }

            $query_msg_create = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$admin_id', '$room_id', 'noti', 'tạo nhóm', current_timestamp, '0', '0')";

            if(!mysqli_query($connect, $query_msg_create)){
                echo "Fail execute query: " .$query_msg_create;
                die();
            }

            $array_member = json_decode($json_member_id, false);

            foreach($array_member as $member){
                $query = "INSERT INTO `tbl_participant` (`user_id`, `room_id`, `nickname`, `isAdmin`, `isHide`, `timestamp`) VALUES ('$member', '$room_id', '', '0', '0', current_timestamp)";
                if(!mysqli_query($connect, $query)){
                    echo "Fail execute query: " .$query;
                    die();
                }

                $query_get_user = "SELECT * FROM tbl_user WHERE id = $member";
                $query_get_user_rs = mysqli_query($connect, $query_get_user);

                $user_name = "";
                while($row = mysqli_fetch_assoc($query_get_user_rs)){
                    $user_name = $row['name'];
                }

                $str = "thêm " . $user_name;

                $query_add_user = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$admin_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

                if(!mysqli_query($connect, $query_add_user)){
                    echo "Fail execute query: " .$query_add_user;
                    die();
                }
            }

            echo "success";
            die();

        }else{
            echo "Fail execute query: " .$query_create_group;
            die();
        }
    }

    if($postObj['method_name'] == 'method_get_messages'){
        $room_id = $postObj['room_id'];
        $user_id = $postObj['user_id'];

        $query = "SELECT DISTINCT z.* FROM
        (
        SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
                INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
                INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
                INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id AND tbl_participant.room_id = tbl_room.id)
                WHERE tbl_room.id = $room_id AND tbl_message.type != 'leave'
        UNION

        SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, '' as nickname FROM tbl_message
                INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
                INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
                WHERE tbl_message.type = 'leave' AND tbl_room.id = $room_id
        ) as z
        WHERE z.time > (SELECT tbl_participant.timestamp FROM tbl_participant WHERE tbl_participant.user_id = $user_id AND tbl_participant.room_id = $room_id)
        ORDER BY z.time";

        $array_message = array();

        $query_rs =  mysqli_query($connect, $query);

        while($row = mysqli_fetch_assoc($query_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll = array();
        $jsObjAll['array_message'] = $array_message;

        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_send_message'){

        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];
        $type = $postObj['type'];
        $message = $postObj['message'];
        $is_send_image = $postObj['is_send_image'];

        if($is_send_image == 'true'){
            $message = rand(0, 99999) . "_" . $_FILES['image']['name'];

            $image_path = 'image/image_message/' . $message;

            if(!move_uploaded_file($_FILES['image']['tmp_name'], $image_path)){
                echo "fail upload";
                die();
            }
        }

        $query = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$user_id', '$room_id', '$type', '$message', current_timestamp(), '0', '0')";

        if(mysqli_query($connect, $query)){

            $message_id = mysqli_insert_id($connect);

            $query_get_msg = "SELECT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
            INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
            INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
            INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id && tbl_participant.room_id = tbl_room.id)
            WHERE tbl_message.id = $message_id";
            $query_get_msg_rs = mysqli_query($connect, $query_get_msg);

            $array_message = array();

            while($row = mysqli_fetch_array($query_get_msg_rs)){
                $data['id'] = $row['id'];
                $data['user_id'] = $row['user_id'];
                $data['room_id'] = $row['room_id'];
                $data['type'] = $row['type'];
                $data['message'] = $row['message'];
                $data['time'] = $row['time'];
                $data['isRemove'] = $row['isRemove'];
                $data['isSeen'] = $row['isSeen'];
                $data['name'] = $row['name'];
                $data['image'] = $row['image'];
                $data['nickname'] = $row['nickname'];

                array_push($array_message, $data);
            }

            $jsObjAll['array_message'] = $array_message;
            echo json_encode($jsObjAll);
        }else{
            echo "query fail";
        }

        die();
    }

    if($postObj['method_name'] == 'method_remove_message'){
        $message_id = $postObj['message_id'];

        $query = "UPDATE tbl_message SET isRemove = '1' WHERE id = $message_id";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail";
        }

        die();
    }

    if($postObj['method_name'] == 'method_seen_message'){
        $message_id = $postObj['message_id'];

        $query = "UPDATE tbl_message SET isSeen = '1' WHERE id = $message_id";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail";
        }

        die();
    }

    if($postObj['method_name'] == 'method_unhide_room'){
        $room_id = $postObj['room_id'];
        $user_id = $postObj['user_id'];

        $query = "UPDATE tbl_participant SET isHide = '0' WHERE `room_id` = $room_id AND `user_id` = $user_id";

        if(mysqli_query($connect, $query)){
            echo "success";
        }

        die();
    }

    if($postObj['method_name'] == 'method_change_group_name'){
        $name = $postObj['name'];
        $room_id = $postObj['room_id'];
        $user_id = $postObj['user_id'];
            $data['email'] = $row['email'];
            $data['isOnline'] = $row['isOnline'];

        $str = "đổi tên nhóm thành "  .$name;

        $query_room = "UPDATE tbl_room SET `name` = '$name' WHERE id = $room_id";
        $query_message = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$user_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

        if(!mysqli_query($connect, $query_room)){
            echo "query fail: ".$query_room;
        }

        if(!mysqli_query($connect, $query_message)){
            echo "query fail: ".$query_message;
        }

        $msg_id = mysqli_insert_id($connect);

        $query_get_msg = "SELECT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id && tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.id = $msg_id";
        $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

        $array_message = array();

        while($row = mysqli_fetch_array($query_get_msg_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll['array_message'] = $array_message;
        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_get_room'){
        $room_id = $postObj['room_id'];
        $user_id = $postObj['user_id'];
        $type = $postObj['type'];

        $jsObj_room = array();
        $jsObj_participant = array();

        if($type == 'private'){

            $query = "SELECT tbl_room.*, tbl_user.name as `username`, tbl_participant.nickname, tbl_user.image as `userimage` FROM `tbl_participant`
                    INNER JOIN tbl_room ON tbl_participant.room_id = tbl_room.id
                    INNER JOIN tbl_user ON tbl_participant.user_id = tbl_user.id
                    WHERE tbl_room.id = $room_id AND tbl_user.id != $user_id";
            $query_rs = mysqli_query($connect, $query);

            while($row = mysqli_fetch_assoc($query_rs)){
                $data['id'] = $row['id'];

                if($row['nickname'] == ""){
                    $data['name'] = $row['username'];
                }else{
                    $data['name'] = $row['nickname'];
                }

                $data['image'] = $row['userimage'];
                $data['background'] = $row['background'];
                $data['type'] = $row['type'];

                array_push($jsObj_room, $data);
            }

        }else{
            $query = "SELECT * FROM tbl_room WHERE id = $room_id";
            $query_rs = mysqli_query($connect, $query);

            while($row = mysqli_fetch_assoc($query_rs)){
                $data1['id'] = $row['id'];
                $data1['name'] = $row['name'];
                $data1['image'] = $row['image'];
                $data1['background'] = $row['background'];
                $data1['type'] = $row['type'];

                array_push($jsObj_room, $data1);
            }
        }

        $query_ptcp = "SELECT * FROM tbl_participant WHERE `room_id` = " .$room_id. " AND `user_id` = $user_id";

        $query_ptcp_rs = mysqli_query($connect, $query_ptcp);
        while($row = mysqli_fetch_assoc($query_ptcp_rs)){

            $data3['user_id'] = $row['user_id'];
            $data3['room_id'] = $row['room_id'];
            $data3['nickname'] = $row['nickname'];
            $data3['isAdmin'] = $row['isAdmin'];
            $data3['isHide'] = $row['isHide'];
            $data3['timestamp'] = $row['timestamp'];

            array_push($jsObj_participant, $data3);
        }
        $jsObjAll['array_room'] = $jsObj_room;
        $jsObjAll['array_participant'] = $jsObj_participant;

        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_change_group_image'){
        $room_id = $postObj['room_id'];
        $user_id = $postObj['user_id'];

        $query_get_room = "SELECT * FROM tbl_room WHERE id = $room_id";
        $query_get_room_rs = mysqli_query($connect, $query_get_room);

        $current_image = null;

        if($row = mysqli_fetch_assoc($query_get_room_rs)){
            $current_image = $row['image'];
        }

        $current_image_path = 'image/image_room/' . $current_image;

        if(file_exists($current_image_path)){
            unlink($current_image_path);
        }

        $room_image = rand(0, 99999) . "_" . $_FILES['image']['name'];

        $image_path = 'image/image_room/' . $room_image;

        if(!move_uploaded_file($_FILES['image']['tmp_name'], $image_path)){
            echo "fail upload";
            die();
        }

        $str = "đổi ảnh nhóm";

        $query = "UPDATE tbl_room SET `image` = '$room_image' WHERE id = '$room_id'";
        $query_message = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$user_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

        if(!mysqli_query($connect, $query)){
            echo "query fail: ".$query;
            die();
        }

        if(!mysqli_query($connect, $query_message)){
            echo "query fail: ".$query_message;
            die();
        }

        $msg_id = mysqli_insert_id($connect);

        $query_get_msg = "SELECT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id && tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.id = $msg_id";
        $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

        $array_message = array();

        while($row = mysqli_fetch_array($query_get_msg_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll['array_message'] = $array_message;
        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_get_group_unmember'){
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];

        $jsObj_user = array();

        $query = "SELECT * FROM `tbl_relationship`
        INNER JOIN tbl_user ON (tbl_user.id = user_id1 OR tbl_user.id = user_id2)
        WHERE (tbl_user.id != $user_id AND tbl_relationship.status = 'friend' AND (tbl_relationship.user_id1 = $user_id OR tbl_relationship.user_id2 = $user_id))
        AND tbl_user.id NOT IN
        (
            SELECT tbl_user.id FROM `tbl_relationship`
            INNER JOIN tbl_user ON (tbl_user.id = user_id1 OR tbl_user.id = user_id2)
            INNER JOIN tbl_participant ON tbl_participant.user_id = tbl_user.id AND tbl_participant.room_id = $room_id
            WHERE (tbl_user.id != $user_id AND tbl_relationship.status = 'friend' AND (tbl_relationship.user_id1 = $user_id OR tbl_relationship.user_id2 = $user_id))
        )
        ";

        $query_rs =  mysqli_query($connect, $query);

        while($row = mysqli_fetch_assoc($query_rs)){
            $data2['id'] = $row['id'];
            $data2['name'] = $row['name'];
            $data2['image'] = $row['image'];
            $data2['birthday'] = $row['birthday'];
            $data2['phone'] = $row['phone'];
            $data2['email'] = $row['email'];
            $data2['bio'] = $row['bio'];
            $data2['isOnline'] = $row['isOnline'];

            array_push($jsObj_user, $data2);
        }

        $jsObjAll['array_user'] = $jsObj_user;

        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_add_member'){
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];
        $json_member_id = $postObj['json_member_id'];

        $array_member = json_decode($json_member_id, false);

        $array_message = array();

        foreach($array_member as $member){
            $query_insert_member = "INSERT INTO `tbl_participant` (`user_id`, `room_id`, `nickname`, `isAdmin`, `isHide`, `timestamp`) VALUES ('$member', '$room_id', '', '0', '0', current_timestamp)";

            if(!mysqli_query($connect, $query_insert_member)){
                echo "query fail" .$query_insert_member;
                die();
            }

            $query_get_user = "SELECT * FROM tbl_user WHERE `id` = $member";
            $query_get_user_rs = mysqli_query($connect, $query_get_user) or die("query fail: " .$query_get_user);

            while($row = mysqli_fetch_assoc($query_get_user_rs)){
                $user_name = $row['name'];
            }

            $str = "thêm " . $user_name;
            $query_noti_msg = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$user_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

            if(!mysqli_query($connect, $query_noti_msg)){
                echo "query fail" .$query_noti_msg;
                die();
            }

            $msg_id = mysqli_insert_id($connect);

            $query_get_msg = "SELECT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
            INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
            INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
            INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id && tbl_participant.room_id = tbl_room.id)
            WHERE tbl_message.id = $msg_id";
            $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

            while($row = mysqli_fetch_array($query_get_msg_rs)){
                $data['id'] = $row['id'];
                $data['user_id'] = $row['user_id'];
                $data['room_id'] = $row['room_id'];
                $data['type'] = $row['type'];
                $data['message'] = $row['message'];
                $data['time'] = $row['time'];
                $data['isRemove'] = $row['isRemove'];
                $data['isSeen'] = $row['isSeen'];
                $data['name'] = $row['name'];
                $data['image'] = $row['image'];
                $data['nickname'] = $row['nickname'];

                array_push($array_message, $data);
            }

        }

        $jsObjAll['array_message'] = $array_message;
        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_get_memberlist'){
        $room_id = $postObj['room_id'];

        $query = "SELECT * FROM `tbl_participant`
        INNER JOIN tbl_user ON tbl_user.id = tbl_participant.user_id
        WHERE tbl_participant.room_id = $room_id";

        $jsObj_user = array();
        $jsObj_participant = array();

        $query_rs = mysqli_query($connect, $query);

        while($row = mysqli_fetch_assoc($query_rs)){
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['nickname'] = $row['nickname'];
            $data['isAdmin'] = $row['isAdmin'];
            $data['isHide'] = $row['isHide'];
            $data['timestamp'] = $row['timestamp'];

            array_push($jsObj_participant, $data);

            $data1['id'] = $row['id'];
            $data1['name'] = $row['name'];
            $data1['image'] = $row['image'];
            $data1['birthday'] = $row['birthday'];
            $data1['phone'] = $row['phone'];
            $data1['email'] = $row['email'];
            $data1['bio'] = $row['bio'];
            $data1['isOnline'] = $row['isOnline'];

            array_push($jsObj_user, $data1);
        }

        $jsObjAll['array_user'] = $jsObj_user;
        $jsObjAll['array_participant'] = $jsObj_participant;

        echo json_encode($jsObjAll);

        die();
    }

    if($postObj['method_name'] == 'method_set_group_admin'){

        $admin_id = $postObj['admin_id'];
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];
        $is_admin = $postObj['is_admin'];

        if($is_admin == "true"){
            $state = 1;
        }else{
            $state = 0;
        }

        $query = "UPDATE `tbl_participant` SET `isAdmin` = '$state' WHERE `tbl_participant`.`user_id` = $user_id AND `tbl_participant`.`room_id` = $room_id";

        if(!mysqli_query($connect, $query)){
            echo "query fail" .$query;
            die();
        }

        $query_get_user = "SELECT * FROM tbl_user WHERE `id` = $user_id";
        $query_get_user_rs = mysqli_query($connect, $query_get_user) or die("query fail: " .$query_get_user);

        while($row = mysqli_fetch_assoc($query_get_user_rs)){
            $user_name = $row['name'];
        }

        if($is_admin == 'false'){
            $str = "hủy quyền quản trị viên của " . $user_name;
        }else{
            $str = "chỉ định " . $user_name ." làm quản trị viên";
        }

        $query_noti_msg = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$admin_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

        if(!mysqli_query($connect, $query_noti_msg)){
            echo "query fail" .$query_noti_msg;
            die();
        }

        $msg_id = mysqli_insert_id($connect);

        $query_get_msg = "SELECT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id && tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.id = $msg_id";
        $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

        $array_message = array();

        while($row = mysqli_fetch_array($query_get_msg_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll['array_message'] = $array_message;
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

    if($postObj['method_name'] == 'method_delete_group_member'){
        $admin_id = $postObj['admin_id'];
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];

        $query = "DELETE FROM `tbl_participant` WHERE `tbl_participant`.`user_id` = $user_id AND `tbl_participant`.`room_id` = $room_id";

        if(!mysqli_query($connect, $query)){
            echo "query fail" .$query;
            die();
        }

        $query_get_user = "SELECT * FROM tbl_user WHERE `id` = $user_id";
        $query_get_user_rs = mysqli_query($connect, $query_get_user) or die("query fail: " .$query_get_user);

        while($row = mysqli_fetch_assoc($query_get_user_rs)){
            $user_name = $row['name'];
        }

        $str = "xóa " . $user_name ." ra khỏi nhóm";

        $query_noti_msg = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$admin_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

        if(!mysqli_query($connect, $query_noti_msg)){
            echo "query fail" .$query_noti_msg;
            die();
        }

        $msg_id = mysqli_insert_id($connect);

        $query_get_msg = "SELECT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id && tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.id = $msg_id";
        $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

        $array_message = array();

        while($row = mysqli_fetch_array($query_get_msg_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll['array_message'] = $array_message;
        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_leave_room'){
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];

        $query = "DELETE FROM `tbl_participant` WHERE `tbl_participant`.`user_id` = $user_id AND `tbl_participant`.`room_id` = $room_id";

        if(!mysqli_query($connect, $query)){
            echo "query fail" .$query;
            die();
        }

        $query_get_user = "SELECT * FROM tbl_user WHERE `id` = $user_id";
        $query_get_user_rs = mysqli_query($connect, $query_get_user) or die("query fail: " .$query_get_user);

        while($row = mysqli_fetch_assoc($query_get_user_rs)){
            $user_name = $row['name'];
        }

        $str = "rời khỏi nhóm";

        $query_noti_msg = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$user_id', '$room_id', 'leave', '$str', current_timestamp, '0', '0')";

        if(!mysqli_query($connect, $query_noti_msg)){
            echo "query fail" .$query_noti_msg;
            die();
        }

        $msg_id = mysqli_insert_id($connect);

        $query_get_msg = "SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.id = $msg_id";
        $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

        $array_message = array();

        while($row = mysqli_fetch_array($query_get_msg_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = "";

            array_push($array_message, $data);
        }

        $jsObjAll['array_message'] = $array_message;
        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_check_last_admin'){
        $room_id = $postObj['room_id'];

        $query = "SELECT * FROM `tbl_participant` WHERE `room_id` = $room_id AND `isAdmin` = 1";
        $query_rs = mysqli_query($connect, $query);
        $row_count = mysqli_num_rows($query_rs);

        if($row_count > 1){
            echo "success";
        }else{
            echo "fail";
        }

        die();

    }

    if($postObj['method_name'] == 'method_search_message'){
        $room_id = $postObj['room_id'];
        $text = $postObj['text'];

        $query = "SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id AND tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.room_id = $room_id AND tbl_message.type = 'text' AND tbl_message.message LIKE '%$text%' AND `isRemove` = 0";

        $array_message = array();

        $query_rs =  mysqli_query($connect, $query);

        while($row = mysqli_fetch_assoc($query_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll = array();
        $jsObjAll['array_message'] = $array_message;

        echo json_encode($jsObjAll);
        die();

    }

    if($postObj['method_name'] == 'method_image_message'){
        $room_id = $postObj['room_id'];

        $query = "SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.user_id = tbl_user.id AND tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.room_id = $room_id AND tbl_message.type = 'image' AND `isRemove` = 0";

        $array_message = array();

        $query_rs =  mysqli_query($connect, $query);

        while($row = mysqli_fetch_assoc($query_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);

        }

        $jsObjAll = array();
        $jsObjAll['array_message'] = $array_message;

        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_change_nickname'){
        $admin_id = $postObj['admin_id'];
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];
        $nickname = $postObj['nickname'];

        $query = "UPDATE `tbl_participant` SET `nickname` = '$nickname' WHERE `tbl_participant`.`user_id` = $user_id AND `tbl_participant`.`room_id` = $room_id";

        if(!mysqli_query($connect, $query)){
            echo "query fail" .$query;
            die();
        }

        $query_get_user = "SELECT * FROM tbl_user WHERE `id` = $user_id";
        $query_get_user_rs = mysqli_query($connect, $query_get_user) or die("query fail: " .$query_get_user);

        while($row = mysqli_fetch_assoc($query_get_user_rs)){
            $user_name = $row['name'];
        }

        $str = "đặt biệt danh " . $user_name . " thành " .$nickname;

        $query_noti_msg = "INSERT INTO `tbl_message` (`id`, `user_id`, `room_id`, `type`, `message`, `time`, `isRemove`, `isSeen`) VALUES (NULL, '$admin_id', '$room_id', 'noti', '$str', current_timestamp, '0', '0')";

        if(!mysqli_query($connect, $query_noti_msg)){
            echo "query fail" .$query_noti_msg;
            die();
        }

        $msg_id = mysqli_insert_id($connect);

        $query_get_msg = "SELECT DISTINCT tbl_message.*, tbl_user.name, tbl_user.image, tbl_participant.nickname FROM tbl_message
        INNER JOIN tbl_user ON tbl_user.id = tbl_message.user_id
        INNER JOIN tbl_room ON tbl_room.id = tbl_message.room_id
        INNER JOIN tbl_participant ON (tbl_participant.room_id = tbl_room.id)
        WHERE tbl_message.id = $msg_id";
        $query_get_msg_rs = mysqli_query($connect, $query_get_msg) or die("query fail:" . $query_get_msg);

        $array_message = array();

        while($row = mysqli_fetch_array($query_get_msg_rs)){
            $data['id'] = $row['id'];
            $data['user_id'] = $row['user_id'];
            $data['room_id'] = $row['room_id'];
            $data['type'] = $row['type'];
            $data['message'] = $row['message'];
            $data['time'] = $row['time'];
            $data['isRemove'] = $row['isRemove'];
            $data['isSeen'] = $row['isSeen'];
            $data['name'] = $row['name'];
            $data['image'] = $row['image'];
            $data['nickname'] = $row['nickname'];

            array_push($array_message, $data);
        }

        $jsObjAll['array_message'] = $array_message;
        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_delete_history'){
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];

        $query = "UPDATE tbl_participant SET `timestamp` = current_timestamp WHERE `user_id` = $user_id AND `room_id` = $room_id";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail: " . $query;
        }

        die();
    }

    if($postObj['method_name'] == 'method_msg_block_user'){
        $user_id = $postObj['user_id'];
        $admin_id = $postObj['admin_id'];

        $query = "UPDATE `tbl_relationship` SET `blocker` = '$admin_id', `status` = 'block'
        WHERE (`tbl_relationship`.`user_id1` = $admin_id AND `tbl_relationship`.`user_id2` = $user_id) OR
        (`tbl_relationship`.`user_id1` = $user_id AND `tbl_relationship`.`user_id2` = $admin_id)";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail: " . $query;
        }

        die();
    }

    if($postObj['method_name'] == 'method_get_user_relationship'){
        $user_id = $postObj['user_id'];
        $admin_id = $postObj['admin_id'];

        $query = "SELECT * FROM tbl_relationship WHERE (`tbl_relationship`.`user_id1` = $admin_id AND `tbl_relationship`.`user_id2` = $user_id) OR
        (`tbl_relationship`.`user_id1` = $user_id AND `tbl_relationship`.`user_id2` = $admin_id)";

        $query_rs =  mysqli_query($connect, $query);

        $array_relation = array();

        while($row = mysqli_fetch_assoc($query_rs)){
            $relation['user_id1'] = $row['user_id1'];
            $relation['user_id2'] = $row['user_id2'];
            $relation['requester'] = $row['requester'];
            $relation['blocker'] = $row['blocker'];
            $relation['status'] = $row['status'];

            array_push($array_relation, $relation);
        }

        $jsObjAll['array_relation'] = $array_relation;
        echo json_encode($jsObjAll);
        die();
    }

    if($postObj['method_name'] == 'method_msg_unblock'){
        $user_id = $postObj['user_id'];
        $admin_id = $postObj['admin_id'];

        $query = "UPDATE `tbl_relationship` SET `blocker` = null, `status` = 'friend'
        WHERE (`tbl_relationship`.`user_id1` = $admin_id AND `tbl_relationship`.`user_id2` = $user_id) OR
        (`tbl_relationship`.`user_id1` = $user_id AND `tbl_relationship`.`user_id2` = $admin_id)";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail: " . $query;
        }

        die();
    }

    if($postObj['method_name'] == 'method_hide_room'){
        $user_id = $postObj['user_id'];
        $room_id = $postObj['room_id'];

        $query = "UPDATE tbl_participant SET `isHide` = 1 WHERE `user_id` = $user_id AND `room_id` = $room_id";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail: " . $query;
        }

        die();
    }

    if($postObj['method_name'] == 'method_update_online'){
        $user_id = $postObj['user_id'];
        $status = $postObj['status'];

        $query = "UPDATE tbl_user SET isOnline = $status WHERE `id` = $user_id";

        if(mysqli_query($connect, $query)){
            echo "success";
        }else{
            echo "query fail: " .$query;
        }

        die();
    }

?>