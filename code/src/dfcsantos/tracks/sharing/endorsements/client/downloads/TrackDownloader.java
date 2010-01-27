package dfcsantos.tracks.sharing.endorsements.client.downloads;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface TrackDownloader {

	Signal<Integer> numberOfDownloadedTracks();

	void setActive(boolean isActive);

}