    SELECT
        sum( A.consume_time ) time,
        count( A.row_id ) total
    FROM
        to_dos A
    WHERE
        A.del_flag = 0
        AND A.`status` = 2
        AND A.user_id =:userId
        and A.show_flag=1
    GROUP BY
        A.user_id