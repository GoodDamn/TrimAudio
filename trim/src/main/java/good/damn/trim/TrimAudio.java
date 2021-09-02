package good.damn.trim;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TrimAudio {

    private final Context context;
    private byte[] audioBytes;

    public TrimAudio(Context context, Uri audioUri)
    {
        this.context = context;
        setAudioSource(audioUri);
    }

    public TrimAudio(Context context)
    {
        this.context = context;
    }

    public MediaPlayer trim(int fromSeconds, int toSeconds, int bitrate, MediaPlayer mediaPlayer)
    {
        try
        {
            int bytesBegin = fromSeconds * bitrate / 8 * 1024,
                    bytesEnd = toSeconds * bitrate / 8 * 1024;
            byte[] trimAudio = Arrays.copyOfRange(audioBytes, bytesBegin, bytesEnd); // Trim audio in bytes
            File mp3Cache = File.createTempFile("cache",  "mp3", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(mp3Cache);
            fos.write(trimAudio); // Write bytes at cache file

            if (mediaPlayer != null)
                mediaPlayer.reset();
            else mediaPlayer = new MediaPlayer();

            // Read audio file
            FileInputStream fileInputStream = new FileInputStream(mp3Cache);
            mediaPlayer.setDataSource(fileInputStream.getFD());
            fileInputStream.close();
            // Prepare media player
            mediaPlayer.prepare();
        } catch (IOException e) { e.printStackTrace(); }
        return mediaPlayer;
    }

    public void setAudioSource(Uri audioUri)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream br;
        try {
            br = context.getContentResolver().openInputStream(audioUri);
            byte[] bytes = new byte[1024];
            int n;
            while (-1 != (n = br.read(bytes)))
                baos.write(bytes, 0, n);
        } catch (IOException e) { e.printStackTrace(); }
        audioBytes = baos.toByteArray();
    }

}
