package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.RequiredArgsConstructor;
import script.SCRIPT;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import view.main.VIEW;

import java.io.IOException;

/**
 * Represents one instance of the script.
 * See {@link SCRIPT.SCRIPT_INSTANCE} for some documentation
 */
@RequiredArgsConstructor
final class Instance implements SCRIPT.SCRIPT_INSTANCE {

	private final static Logger log = Loggers.getLogger(Instance.class);

	private boolean init = false;

	private boolean initGamePresent = false;

	private final MoreOptionsScript script;

	@Override
	public void update(double v) {
		if (!init) {
			log.debug("initGameRunning");
			script.initGameRunning();
			init = true;
		}

		if (!initGamePresent && !VIEW.inters().load.isActivated()) {
			log.debug("initGamePresent");
			script.initGamePresent();
			initGamePresent = true;
		}
	}

	@Override
	public void save(FilePutter filePutter) {

	}

	@Override
	public void load(FileGetter fileGetter) throws IOException {

	}
}