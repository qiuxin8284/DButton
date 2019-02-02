package com.jordan.httplibrary.utils.data;

public class ResponseData {
    private String mResultCode, mMessage;

    public ResponseData(String ResultCode, String Message) {
        mResultCode = ResultCode;
        mMessage = Message;
    }

    public String getResultCode() {
        return mResultCode;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "mResultCode='" + mResultCode + '\'' +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }
}
