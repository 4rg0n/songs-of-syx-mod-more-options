package com.github.argon.sos.moreoptions.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Holds common mod configuration, which can be used in the game
 *
 * Still very much WIP
 */
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModProperties {
   private String errorReportUrl;
}
