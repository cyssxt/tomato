    SELECT
        *
    FROM
        (
        SELECT
            DATE_FORMAT( A.finish_time, '%Y%m' ) AS full_month,
            DATE_FORMAT( A.finish_time, '%d' ) AS day_no,
            A.title,
            A.STATUS,
            A.finish_time,
            A.row_id,
            0 AS type
        FROM
            to_dos A
        WHERE
            A.del_flag = 0
            AND A.show_flag = 1
            AND A.user_id = :userId
            AND A.STATUS=2
            AND A.finish_time IS NOT NULL
        ) C
    WHERE
        C.full_month IN :fullMonths