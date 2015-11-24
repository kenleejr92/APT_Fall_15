import java.util.*;

public class MockMp3Player implements Mp3Player {
    /** 
   * Begin playing the filename at the top of the
   * play list, or do nothing if playlist 
   * is empty. 
   */

  boolean isPlaying;
  double position;
  ArrayList<String> songList;
  int songPointer;

  public MockMp3Player(){
    isPlaying = false;
    position = 0.0;
    songPointer = 0;
  }

  public void play(){
	if(songList == null) return;
	isPlaying = true;
	position += 1.0;
  }

  /** 
   * Pause playing. Play will resume at this spot. 
   */
  public void pause(){
	isPlaying = false;
  }

  /** 
   * Stop playing. The current song remains at the
   * top of the playlist, but rewinds to the 
   * beginning of the song.
   */
  public void stop(){
	position = 0.0;
	isPlaying = false;
  }

  /** Returns the number of seconds into 
   * the current song.
   */
  public double currentPosition(){
	if(songList == null) return 0.0;
	return position;
  }

  
  /**
   * Returns the currently playing file name.
   */
  public String currentSong(){
	return songList.get(songPointer);
  }

  /** 
   * Advance to the next song in the playlist 
   * and begin playing it.
   */
  public void next(){
	if(songPointer == songList.size() - 1)  ;
	else songPointer = songPointer+1;
  }

  /**
   * Go back to the previous song in the playlist
   * and begin playing it.
   */
  public void prev(){
	if(songPointer == 0) songPointer = 0;
	else songPointer = songPointer-1;
  }

  /** 
   * Returns true if a song is currently 
   * being played.
   */
  public boolean isPlaying(){
	return isPlaying;
  }

  /**
   * Load filenames into the playlist.
   */
  public void loadSongs(ArrayList names){
	songList = names;
  }
 
}
