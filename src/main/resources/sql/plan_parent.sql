SELECT
	*
FROM
	(
		(
		SELECT
			D.*,
			B.day_info
		FROM
			(
			SELECT
				DATE_FORMAT( date,'%Y%m%d' ) AS date_no,
				DATE_FORMAT( date,'%d' ) AS day_info,
				date
			FROM
				(
				SELECT
					@s \:= @s + 1 AS `index`,
					DATE_ADD( CURRENT_DATE, INTERVAL @s DAY ) AS `date`
				FROM
					mysql.help_topic,
					( SELECT @s \:= 0 ) temp
				WHERE
					@s < 7
				) A
			) B
			LEFT JOIN (
			SELECT
				A.create_time,
				0 AS content_type,
				COALESCE ( A.repeat_flag, 0 ) AS repeat_flag,
				A.date_no,
				A.parent_id,
				A.parent_type,
				A.plan_time,
				A.title,
				A.row_id AS row_id,
				A.execute_time,

			IF
				( V.action_count > 0, TRUE, FALSE ) AS action_flag,
			IF
				( W.tag_count > 0, TRUE, FALSE ) AS tag_flag
			FROM
				to_dos A
				LEFT JOIN ( SELECT count( T.row_id ) action_count, T.to_do_id FROM to_do_actions T WHERE T.del_flag = 0 GROUP BY T.to_do_id ) V ON V.to_do_id = A.row_id
				LEFT JOIN (
				SELECT
					count( R.tag_id ) AS tag_count,
					R.content_id
				FROM
					re_tag R
				WHERE
					R.del_flag = 0
					AND R.content_type = 0
				GROUP BY
					R.content_id
				) W ON W.content_id = A.row_id
			WHERE
				A.user_id =:userId
				AND A.del_flag = 0
				AND A.status!=2
				AND A.show_flag=1
			GROUP BY
				A.row_id
			) D ON D.date_no = B.date_no
		) UNION ALL
		(
		SELECT
			A.create_time,
			0 AS content_type,
			A.repeat_flag,
			DATE_FORMAT( A.execute_time, '%Y%m%d' ) date_no,
			A.parent_id,
			A.parent_type,
			A.plan_time,
			A.title,
			A.row_id AS row_id,
			A.execute_time,
		IF
			( V.action_count > 0, TRUE, FALSE ) AS action_flag,
		IF
			( W.tag_count > 0, TRUE, FALSE ) AS tag_flag,
			DATE_FORMAT( A.execute_time, '%Y%m%d' ) AS day_info
		FROM
			to_dos A
			LEFT JOIN ( SELECT count( T.row_id ) action_count, T.to_do_id FROM to_do_actions T WHERE T.del_flag = 0 GROUP BY T.to_do_id ) V ON V.to_do_id = A.row_id
			LEFT JOIN (
			SELECT
				count( R.tag_id ) AS tag_count,
				R.content_id
			FROM
				re_tag R
			WHERE
				R.del_flag = 0
				AND R.content_type = 0
			GROUP BY
				R.content_id
			) W ON W.content_id = A.row_id
		WHERE
			A.user_id =:userId
            AND A.STATUS!=2
            AND A.SHOW_FLAG=1
			AND A.del_flag = 0
			AND A.repeat_flag = 1
			AND DATE_FORMAT( A.execute_time, '%Y%m%d' ) > DATE_FORMAT( DATE_ADD( now( ), INTERVAL 7 DAY ), '%Y%m%d' )
		)
	) E
    WHERE
	ifnull( E.row_id, '' ) != ''
