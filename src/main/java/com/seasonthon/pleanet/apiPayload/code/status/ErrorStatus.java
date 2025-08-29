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
    _TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO404", "해당 Todo 항목을 찾을 수 없습니다."),
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404","유효하지 않은 MemberId입니다."),
    _STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE404","유효하지 않은 StoreId입니다."),
    _MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION404","유효하지 않은 MissionId입니다."),
    _MISSION_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "MISSION409", "이미 진행 중인 미션입니다."),
    _REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE404","유효하지 않은 ReviewId입니다."),
    _FILE_REQUIRED(HttpStatus.BAD_REQUEST, "FILE400", "파일이 필요합니다."),
    _IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE404","유효하지 않은 ImageId입니다.");


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
