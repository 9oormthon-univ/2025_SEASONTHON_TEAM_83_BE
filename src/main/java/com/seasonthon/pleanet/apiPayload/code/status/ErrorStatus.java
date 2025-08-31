package com.seasonthon.pleanet.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.seasonthon.pleanet.apiPayload.code.BaseErrorCode;
import com.seasonthon.pleanet.apiPayload.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorStatus implements BaseErrorCode {
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404","존재하지 않는 회원 정보입니다."),
    _EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER405","가입된 계정이 아닙니다."),
    _NICKNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER406","유효하지 않은 이메일입니다."),
    _MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER409", "이미 존재하는 이메일입니다."),
    _NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER410", "이미 존재하는 닉네임입니다."),

    _PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "AUTH401", "비밀번호가 올바르지 않습니다."),

    _MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION404","해당 미션은 존재하지 않거나, 삭제되었습니다."),
    _MISSION_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "MISSION409", "이미 진행 중인 미션입니다."),
    _MISSION_ALREADY_TODAY(HttpStatus.CONFLICT, "MISSION410", "이미 미션에 참여하셨습니다. 내일 다시 도전해 주세요!"),
    _MISSION_NOT_GPS(HttpStatus.BAD_REQUEST, "MISSION400", "해당 미션은 GPS 기반 방식이 아닙니다."),

    _MEMBER_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE404", "참여 중인 미션을 찾을 수 없습니다."),
    _MEMBER_MISSION_ALREADY_COMPLETED(HttpStatus.CONFLICT, "CHALLENGE409", "이미 완료된 미션입니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
