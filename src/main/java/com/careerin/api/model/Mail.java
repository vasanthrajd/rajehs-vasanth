
package com.careerin.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Mail implements Serializable {

    private static final long serialVersionUID = 3534205265386249686L;

    private String fromAddress;

    private String toAddress;

    private String subject;

    private String content;

    private Map<String, String> model;

    public Mail() {
        model = new HashMap<>();
    }


}
