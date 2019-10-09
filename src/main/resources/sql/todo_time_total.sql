    SELECT
        SUM( A.total_time ) total_time
    FROM
        time_action A
    WHERE
        A.del_flag =0
        and A.to_do_id=:todoId