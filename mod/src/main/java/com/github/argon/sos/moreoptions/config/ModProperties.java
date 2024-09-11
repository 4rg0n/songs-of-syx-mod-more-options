package com.github.argon.sos.moreoptions.config;

import lombok.Builder;
import lombok.Data;

/**
 * Holds common mod configuration, which can be used in the game
 *
 * Still very much WIP
 */
@Data
@Builder
public class ModProperties {
   private String errorReportUrl;
}
