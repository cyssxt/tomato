    SELECT
        *
    FROM
        (
        SELECT
            B.title,
            B.finish_time,
            B.row_id,
            Date_format( B.finish_time, '%Y%m%d' ) AS date_no,
            days,
            start_time
        FROM
            project_info B
            LEFT JOIN (
            SELECT
                C.parent_id,
                count( C.row_id ) total,
                row_id
            FROM
                to_dos C
            WHERE
                C.user_id =:userId
                AND C.SHOW_FLAG = 1
                AND C.del_flag = 0
                AND C.parent_type = 1
                AND ( C.`status` != 2 OR C.`status` IS NULL )
            GROUP BY
                C.parent_id
            ) D ON D.parent_id = B.row_id
            LEFT JOIN (
            SELECT
                datediff( D.end_time, D.start_time ) days,
                D.start_time,
                D.row_id
            FROM
                (
                SELECT
                    MAX( C.finish_time ) END_TIME,
                    MIN( C.create_time ) start_time,
                    C.row_id
                FROM
                    (
                    SELECT
                        A.finish_time,
                        A.create_time,
                        A.row_id
                    FROM
                        project_info A
                    WHERE
                        A.del_flag = 0
                        AND A.user_id =:userId UNION ALL
                    SELECT
                        B.finish_time,
                        B.create_time,
                        B.parent_id AS row_id
                    FROM
                        to_dos B
                    WHERE
                        B.parent_id IN ( SELECT A.row_id FROM project_info A WHERE A.del_flag = 0 AND A.user_id =:userId )
                        AND B.parent_type = 1
                        AND B.del_flag = 0
                        AND B.SHOW_FLAG=1
                    ) C
                GROUP BY
                    C.row_id
                ) D
            ) E ON E.row_id = B.row_id
    WHERE
        B.del_flag =0