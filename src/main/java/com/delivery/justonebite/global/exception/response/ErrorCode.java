package com.delivery.justonebite.global.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 예외가 생길때마다 이런식으로 추가
    // 인증/인가
    INVALID_MEMBER("유효하지 않은 사용자입니다.", HttpStatus.FORBIDDEN),
    NOT_VALID_TOKEN("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACCESS("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    DELETED_ACCOUNT("탈퇴 처리된 유저입니다.", HttpStatus.FORBIDDEN),

    // 유저
    ROLE_NOT_FOUND("권한을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("사용중인 이메일입니다.", HttpStatus.CONFLICT),
    NOT_FOUND_USER("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND("주소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 공통
    INVALID_INPUT_DATA("잘못된 입력 데이터입니다.", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_VIOLATION("데이터 무결성에 위배되었습니다.", HttpStatus.CONFLICT),
    UNSUPPORTED_MEDIA_TYPE_ERROR("지원하지 않는 미디어 타입입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    RESOURCE_NOT_FOUND("리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED("지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 상품 AI 응답 생성 에러
    INVALID_AI_RESPONSE("AI API 서버의 에러가 존재합니다. 다시 시도하거나 상품에 대한 프롬프트를 입력해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 가게
    SHOP_NOT_FOUND("존재하지 않는 가게입니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_SHOP_ACCESS("본인의 가게만 삭제할 수 있습니다", HttpStatus.FORBIDDEN),
    ALREADY_PENDING_DELETE("이미 승인 대기중인 삭제 요청입니다.",HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다.",HttpStatus.NOT_FOUND),
    NOT_COMPLETED_ORDER_EXISTS("완료되지 않은 주문이 존재합니다. 가게를 삭제할 수 없습니다.",HttpStatus.BAD_REQUEST),

    //리뷰
    INVALID_RATING_RANGE("평점은 1~5 범위여야 합니다.", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS("해당 주문에는 이미 리뷰가 존재합니다.", HttpStatus.CONFLICT),
    ORDER_NOT_COMPLETED("주문이 아직 완료상태가 아닙니다.", HttpStatus.FORBIDDEN),
    ALREADY_DELETED_REVIEW("이미 삭제된 리뷰입니다.", HttpStatus.CONFLICT),
    ALREADY_ACTIVE_REVIEW("이미 삭제되지 않은 리뷰입니다.", HttpStatus.CONFLICT),


    //주문
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_STATUS_NOT_FOUND("주문상태를 확인할수 없습니다", HttpStatus.NOT_FOUND),
    INVALID_ITEM("존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_ROLE("유효하지 않은 회원 유형입니다.", HttpStatus.FORBIDDEN),
    INVALID_ORDER_STATUS("유효하지 않은 주문 상태입니다.", HttpStatus.BAD_REQUEST),
    TOTAL_PRICE_NOT_MATCH("전체 주문금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_CANCEL_NOT_ALLOWED("취소할 수 없는 주문 상태입니다.", HttpStatus.BAD_REQUEST),
    ORDER_CANCEL_TIME_EXCEEDED("주문 시점으로부터 5분이 경과하여 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ORDER_USER_NOT_MATCH("주문 상의 주문자와 동일한 회원이 아닙니다.", HttpStatus.FORBIDDEN),
    INVALID_CANCEL_STATUS_VALUE("취소 요청 상태는 ORDER_CANCELLED 여야 합니다.", HttpStatus.BAD_REQUEST),

    //결제
    PAYMENT_NOT_FOUND("결제 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PAYMENT_STATUS("승인이 불가한 결제 상태입니다.",HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_EXISTS("이미 결제가 완료된 주문입니다.", HttpStatus.CONFLICT),
    PAYMENT_AMOUNT_NOT_MATCH("결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    CANCEL_AMOUNT_EXCEEDED("요청 금액이 취소 가능 금액보다 큽니다.",HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_CANCELED("이미 취소된 결제 내역입니다.", HttpStatus.CONFLICT),
    PAYMENT_CONFIRM_FAILED("결제 승인에 실패했습니다.",HttpStatus.INTERNAL_SERVER_ERROR);


    private final String description;
    private final HttpStatus status;

}
