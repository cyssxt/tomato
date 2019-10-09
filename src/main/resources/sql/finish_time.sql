    SELECT
        *
    FROM
        (
        SELECT
            DATE_FORMAT( A.finish_time, '%Y%m' ) AS full_month,
            DATE_FORMAT( A.finish_time, '%m' ) AS month_no
        FROM
            to_dos A
        WHERE
            A.del_flag = 0
            AND A.show_flag = 1
            AND A.`status` = 2
            AND A.finish_time IS NOT NULL
            AND A.user_id = :userId
        GROUP BY
            month_no
        ) A
    ORDER BY
        A.month_no DESC