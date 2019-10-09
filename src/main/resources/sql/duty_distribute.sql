    SELECT
        E.title,
        E.row_id,
        sum( E.time ) time,
        color
    FROM
        (
            (
            SELECT
                D.title,
                D.row_id,
                SUM( A.consume_time ) time,
                D.color
            FROM
                to_dos A,
                duty_info D
            WHERE
                DATE_FORMAT( A.finish_time, '%Y%m%d' ) >=:start
                AND DATE_FORMAT( A.finish_time, '%Y%m%d' ) <=:end
                AND A.parent_type = 2
                AND A.parent_id = D.row_id
                AND A.user_id =:userId
                AND A.`status` =:status
                AND A.show_flag=1
            GROUP BY
                D.row_id
            ) UNION ALL
            (
            SELECT
                D.title,
                D.row_id,
                SUM( A.consume_time ) time,
                D.color
            FROM
                to_dos A,
                project_info C,
                duty_info D
            WHERE
                DATE_FORMAT( A.finish_time, '%Y%m%d' ) >=:start
                AND DATE_FORMAT( A.finish_time, '%Y%m%d' ) <=:end
                AND A.parent_type = 1
                AND A.user_id =:userId
                AND A.parent_id = C.row_id
                AND C.parent_id = D.row_id
                AND A.`status` =:status
                AND A.show_flag=1
            GROUP BY
                D.row_id
            )
        ) E
    WHERE
        E.row_id IS NOT NULL
    GROUP BY
        E.row_id
    ORDER BY
        E.time DESC