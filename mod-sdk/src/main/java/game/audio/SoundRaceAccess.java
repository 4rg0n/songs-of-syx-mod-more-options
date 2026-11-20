package game.audio;

import game.audio.SoundRace.Sound;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.LinkedList;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gives access to the package private internals of {@link SoundRace}.
 * Lives in the game package on purpose and does not replace any game class.
 */
public final class SoundRaceAccess {

	private SoundRaceAccess() {
	}

	/**
	 * Gives the {@link SoundRace} its own {@link SoundFile} instances and returns them.
	 * <p>
	 * The game shares {@link SoundFile}s between different {@link SoundRace}s, when they are built from the same
	 * wildcard key, e.g. "ROOM_WORK*". Changing the gain of a shared file would change it for all of them.
	 * After this call the files are exclusive to the given sound.
	 *
	 * @param soundRace to isolate the files for
	 * @return sound files only used by the given sound
	 */
	public static List<SoundFile> isolate(SoundRace soundRace) {
		Map<Sound, Sound> copies = new IdentityHashMap<>();
		List<SoundFile> files = new java.util.ArrayList<>();

		for (int i = 0; i < soundRace.all.length; i++) {
			Sound original = soundRace.all[i];
			if (original == null || original.all.size() == 0) {
				continue;
			}

			// races often share the same sound: copy it only once to keep that
			Sound copy = copies.get(original);
			if (copy == null) {
				copy = copy(original);
				copies.put(original, copy);
				for (SoundFile file : copy.all) {
					files.add(file);
				}
			}

			soundRace.all[i] = copy;
		}

		return files;
	}

	private static Sound copy(Sound original) {
		LinkedList<SoundFile> files = new LinkedList<>();

		for (SoundFile file : original.all) {
			SoundFile copy = new SoundFile(file.key, file.sound);
			copy.gain = file.gain;
			copy.pitch = file.pitch;
			copy.frequency = file.frequency;
			files.add(copy);
		}

		return new Sound(new ArrayList<SoundFile>(files));
	}
}
