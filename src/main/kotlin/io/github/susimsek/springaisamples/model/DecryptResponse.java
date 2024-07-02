package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "Response model for decrypted data", example = """
        {
            "decryptedData": {
                "username": "admin",
                "password": "password"
            },
            "_links": {
                "self": {
                    "href": "http://localhost:8071/api/v1/security/decrypt",
                    "type": "POST"
                },
                "encrypt": {
                    "href": "http://localhost:8071/api/v1/security/encrypt",
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
public class DecryptResponse extends RepresentationModel<DecryptResponse> {
    @Schema(description = "The decrypted data", example = "{\"username\": \"admin\", \"password\": \"password\"}")
    private Object decryptedData;
}