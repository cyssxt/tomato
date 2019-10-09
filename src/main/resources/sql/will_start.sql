    SELECT
        *
    FROM
        (
        SELECT
            getremindminute ( remind_type ) AS remind_minute,
            getui_id AS clientId,
            row_id,
            execute_time,
            row_id as to_do_id,
            push_flag
        FROM
            (
            SELECT COALESCE
                ( E.remind_type, 0 ) remind_type,
                C.getui_id,
                A.row_id,
                A.execute_time,
                A.push_flag
            FROM
                to_dos A
                LEFT JOIN user_setting E ON E.user_id = A.user_id,
                user_push_relation C
            WHERE
                A.user_id = C.user_id
                AND A.DEL_FLAG=0
                AND A.SHOW_FLAG=1
                AND A.execute_time IS NOT NULL
            GROUP BY
                A.row_id
            ) F
        ) G
    WHERE
        DATE_ADD( execute_time, INTERVAL - 1 * remind_minute MINUTE ) <= now( ) AND execute_time >= now( )
        AND ( push_flag = 0 OR push_flag IS NULL )