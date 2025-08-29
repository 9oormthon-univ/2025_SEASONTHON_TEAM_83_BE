package com.seasonthon.pleanet.apiPayload.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.seasonthon.pleanet.apiPayload.code.BaseErrorCode;
import com.seasonthon.pleanet.apiPayload.code.ErrorReasonDto;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{
    private BaseErrorCode code;
    public ErrorReasonDto getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
