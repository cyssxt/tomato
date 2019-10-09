package com.cyssxt.tomato.errors;


import com.cyssxt.common.response.ResponseData;

public interface MessageCode {
    String USER_SHOULD_LOGIN = "user.should.login";
    String NOT_SELF_CONTENT = "not.self.content";
    String CODE_ERROR = "code.error";
    String CODE_SEND_TOO_FAST = "code.send.too.fast";
    String FILE_NOT_FOUND = "file.not.found";
    String USER_NOT_AUTH = "user.not.auth";
    String TODO_DAY_INFO_ERROR = "todo.day.info.error";
    String TODO_NOT_EXIST = "todo.not.exist";
    String TIME_TITLE_NOT_NULL = "time.time.not.null";
    String TIME_DUTY_NOT_NULL = "time.duty.not.null";
    String TIME_DEGREE_NOT_NULL = "time.degree.not.null";
    String CLONE_NOT_SUPPORT = "clone.not.support";
    String DATE_TYPE_NOT_BE_NULL = "date.type.not.be.null";
    String DATE_TYPE_CANNOT_FOUND = "date.type.not.found";
    String CAN_NOT_BE_MODIFY = "can.not.be.modify";
    String PHONE_NUMBER_HAS_EXIST = "phonenumber.has.exist";
    String MONTHNO_OR_AREA_TIME_MUST_EXIST_ONE = "monthno.or.area.time.must.exist.one";
    String SESSION_ID_IS_NOT_NULL = "session.id.is.not.null";
    String TIME_CANNOT_REPEAT_START = "time.cannot.repeat.start";
    String NOT_REPEAT_TODO = "not.repeat.todo";
}
