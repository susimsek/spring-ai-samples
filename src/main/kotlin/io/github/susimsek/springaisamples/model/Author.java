package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Represents an author and their books")
public record Author(
    @Schema(description = "The name of the author", example = "John Doe")
    String author,
    @ArraySchema(schema = @Schema(
        description = "A list of books written by the author", example = "Book 1"))
    List<String> books) {
}