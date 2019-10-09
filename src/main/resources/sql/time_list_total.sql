    SELECT
        COALESCE ( sum( A.total_time ), 0 ) total_time,
        count( A.row_id ) time,
        max(A.end_time) as max_end_time,
        min(A.start_time) as min_start_time
    FROM
        time_action A
    WHERE
        ifnull( A.to_do_id, '' ) != ''
        AND A.del_flag = 0
        AND A.date_no =:dateNo
        AND IFNULL(A.total_time,0)>0
        AND A.user_id =:userId