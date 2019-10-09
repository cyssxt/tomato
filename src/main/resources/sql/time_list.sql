  SELECT
    B.title,
    A.start_time,
    A.end_time,
    A.total_time,
    C.COLOR,
    coalesce(B.concentration_degree,5) as degree
  FROM
    time_action A,
    to_dos B
    LEFT JOIN project_info D ON D.del_flag = 0
    AND B.PARENT_ID = D.ROW_ID
    AND B.PARENT_TYPE = 1
    LEFT JOIN duty_info C ON C.del_flag = 0
    AND (
      ( B.PARENT_ID = C.ROW_ID AND B.PARENT_TYPE = 2 )
      OR ( D.PARENT_ID = C.ROW_ID AND B.PARENT_TYPE = 1 )
    )
  WHERE
    A.del_flag = 0
    AND B.DEL_FLAG = 0
    AND A.TO_DO_ID = B.ROW_ID
    AND A.end_time IS NOT NULL
    and A.user_id=:userId
    AND B.show_flag=1
    AND (DATE_FORMAT(A.start_time,'%Y%m%d')=:dateNo
    or DATE_FORMAT(A.end_time,'%Y%m%d')=:dateNo)
  ORDER BY
    A.create_time asc