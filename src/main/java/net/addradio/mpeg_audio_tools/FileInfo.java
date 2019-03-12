/**
 * Class:    FileInfo<br/>
 * <br/>
 * Created:  16.01.2018<br/>
 * Filename: FileInfo.java<br/>
 * Version:  $Revision: $<br/>
 * <br/>
 * last modified on $Date:  $<br/>
 *               by $Author: $<br/>
 * <br/>
 * @author <a href="mailto:sebastian.weiss@nacamar.de">Sebastian A. Weiss, nacamar GmbH</a>
 * @version $Author: $ -- $Revision: $ -- $Date: $
 * <br/>
 * (c) Sebastian A. Weiss, nacamar GmbH 2018 - All rights reserved.
 */

package net.addradio.mpeg_audio_tools;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import net.addradio.codec.id3.model.ID3Tag;
import net.addradio.codec.mpeg.audio.DecodingResult;
import net.addradio.codec.mpeg.audio.MPEGAudio;
import net.addradio.codec.mpeg.audio.MPEGAudioFileSuffix;
import net.addradio.codec.mpeg.audio.model.MPEGAudioFrame;

/**
 * FileInfo
 */
public class FileInfo {

    /**
     * FileInfo constructor.
     */
    public FileInfo() {
    }

    /**
     * main.
     * @param args {@link String}{@code []}
     */
    public static void main(String[] args) {
        Configurator.initialize(new DefaultConfiguration()).getRootLogger()
                .setLevel(org.apache.logging.log4j.Level.ERROR);

        final File dir = new File("testfiles"); //$NON-NLS-1$
        printFileInfoForAllMPEGAudioFilesInDirectory(dir);
    }

    /**
     * printFileInfoForAllMPEGAudioFilesInDirectory.
     * @param dir {@link File}
     */
    @SuppressWarnings("nls")
    public static final void printFileInfoForAllMPEGAudioFilesInDirectory(final File dir) {
        if (dir.exists() && dir.isDirectory()) {
            System.out.println("# -> " + dir.getAbsolutePath());
            final File[] files = dir.listFiles(MPEGAudioFileSuffix.FILENAME_FILTER);
            for (File file : files) {
                System.out.println();
                printFileInfoForMPEGAudioFile(file);
            }
        }
    }

    /**
     * printFileInfoForMPEGAudioFile.
     * @param file {@link File}
     */
    @SuppressWarnings("nls")
    public static final void printFileInfoForMPEGAudioFile(File file) {
        final DecodingResult dr = MPEGAudio.decode(file);
        System.out.println("### -> " + file.getName());
        System.out.println("\tSkipped Bits During Decoding:\t" + dr.getSkippedBits());
        System.out.println("\tAverage Bitrate:\t\t" + dr.getAverageBitRate());
        System.out.println("\tDuration:\t\t\t" + dr.getDurationMillis());
        System.out.println("- Encoding Details -");
        final Iterator<MPEGAudioFrame> iterator = dr.getAudioFrames().iterator();
        if (iterator.hasNext()) {
            final MPEGAudioFrame next = iterator.next();
            System.out.println("\tLayer:\t\t" + next.getLayer());
            System.out.println("\tVersion:\t" + next.getVersion());
            System.out.println("\tSampling Rate:\t" + next.getSamplingRate());
            System.out.println("\tMode:\t\t" + next.getMode());
            System.out.println("\tMode Extension:\t\t" + next.getModeExtension());
            //            System.out.println("\tVersion:\t" + next.getVersion());
            //            System.out.println("\tVersion:\t" + next.getVersion());
            //            System.out.println("\tVersion:\t" + next.getVersion());
        }
        System.out.println("- ID3 Tags -");
        final List<ID3Tag> id3TagsOnly = dr.getId3Tags();
        for (ID3Tag id3Tag : id3TagsOnly) {
            System.out.println("\t" + id3Tag);
        }
    }

}
