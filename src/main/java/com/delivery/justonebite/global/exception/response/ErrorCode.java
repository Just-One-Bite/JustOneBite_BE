package com.delivery.justonebite.global.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 예외가 생길때마다 이런식으로 추가
    // 인증/인가
    INVALID_MEMBER("유효하지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    NOT_VALID_TOKEN("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACCESS("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 공통
    INVALID_INPUT_DATA("잘못된 입력 데이터입니다.", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_VIOLATION("데이터 무결성에 위배되었습니다.", HttpStatus.CONFLICT),
    UNSUPPORTED_MEDIA_TYPE_ERROR("지원하지 않는 미디어 타입입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    RESOURCE_NOT_FOUND("리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED("지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),


    // 주문
    INVALID_ITEM("존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_ROLE("유효하지 않은 회원 유형입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ORDER_STATUS("유효하지 않은 주문 상태입니다.", HttpStatus.UNAUTHORIZED),
    ORDER_NOT_FOUND("주문 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    ;



    private final String description;
    private final HttpStatus status;

}
