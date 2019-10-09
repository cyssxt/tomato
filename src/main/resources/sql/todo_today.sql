    SELECT
        execute_time AS executeTime,
        rowId,
        title,
        type
    FROM
        (
            (
            SELECT
                A.execute_time,
                A.row_id,
                A.title,
                0 AS type
            FROM
                to_dos A
            WHERE A.date_no =:dateNo
                AND A.user_id = :userId
                and A.del_flag=0
                and A.show_flag=1
            ) UNION ALL
            (
            SELECT
                C.execute_time,
                C.row_id AS rowId,
                C.title,
                1 AS type
            FROM
                project_info C
            WHERE
                C.date_no = :dateNo
                AND ( C.del_flag IS NULL OR C.del_flag = 0 )
                AND C.user_id =:userId
            )
        ) D
    ORDER BY
        executeTime ASC