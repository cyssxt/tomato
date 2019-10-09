    SELECT
        count(1) as percent,finish_degree as degree
    FROM
        (
        SELECT
            DATE_FORMAT( A.finish_time, '%Y%m%d' ) finish_time,
            coalesce(A.finish_degree,0) finish_degree,
            A.row_id
        FROM
            to_dos A
        WHERE
            A.del_flag = 0
            AND A.finish_time IS NOT NULL
            AND A.finish_degree IS NOT NULL
            AND A.user_id =:userId
            and DATE_FORMAT( A.finish_time, '%Y%m%d' )>=:startDateNo
            and DATE_FORMAT( A.finish_time, '%Y%m%d' )<=:endDateNo
            order by finish_degree
        ) B
        GROUP BY finish_degree