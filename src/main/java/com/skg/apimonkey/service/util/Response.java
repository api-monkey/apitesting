package com.skg.apimonkey.service.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    /**
     * Body of the response
     */
    private byte[] body;

    /**
     * Http status line of response.
     */
    private StatusLine statusLine;
    /**
     * Content type of the response.
     */
    private ContentType contentType;

    private Map<String, String> headers;
}
