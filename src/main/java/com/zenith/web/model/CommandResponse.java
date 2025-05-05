package com.zenith.web.model;

import java.util.List;

public record CommandResponse(
    String embed,
    String embedComponent,
    List<String> multiLineOutput
) {
}
