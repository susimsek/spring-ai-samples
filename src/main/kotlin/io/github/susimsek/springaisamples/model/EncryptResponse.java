package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "Response model for encrypted data", example = """
        {
            "jweToken": "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ...",
            "_links": {
                "self": {
                    "href": "http://localhost:8071/api/v1/security/encrypt",
                    "type": "POST"
                },
                "decrypt": {
                    "href": "http://localhost:8071/api/v1/security/decrypt",
                    "type": "POST"
                }
            }
        }
    """)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EncryptResponse extends RepresentationModel<EncryptResponse> {
    @Schema(description = "The generated JWE token", example = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ...")
    private String jweToken;
}