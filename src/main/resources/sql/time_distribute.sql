    select
    count( A.row_id ) as count, sum( A.consume_time ) as time
    FROM
    to_dos A
    WHERE
    DATE_FORMAT(A.finish_time,'%Y%m%d') >=:start
    AND DATE_FORMAT(A.finish_time,'%Y%m%d')<=:end
    AND A.user_id =:userId
    AND A.STATUS =2
    AND A.del_flag = 0
    AND A.show_flag=1