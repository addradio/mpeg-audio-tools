/**
 * Class:    StreamInfo<br/>
 * <br/>
 * Created:  12.06.2018<br/>
 * Filename: StreamInfo.java<br/>
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.addradio.codec.mpeg.audio.MPEGAudio;
import net.addradio.codec.mpeg.audio.model.BitRate;
import net.addradio.codec.mpeg.audio.model.MPEGAudioContent;
import net.addradio.codec.mpeg.audio.model.MPEGAudioFrame;
import net.addradio.codec.mpeg.audio.model.Mode;
import net.addradio.codec.mpeg.audio.model.SamplingRate;
import net.addradio.codec.mpeg.audio.tools.MPEGAudioContentFilter;
import net.addradio.codec.mpeg.audio.tools.MPEGAudioContentHandler;

/**
 * StreamInfo
 */
public class StreamInfo {

    /** {@link Logger} LOG */
    static final Logger LOG = LoggerFactory.getLogger(StreamInfo.class);

    /** {@link ExecutorService} workerThreads */
    static final ExecutorService workerThreads = Executors.newCachedThreadPool();

    /**
     * main.
     * @param args {@link String}{@code []}
     */
    public static void main(final String[] args) {
        try (final InputStream is = new URL("http://62.27.87.102:80/rt1/suedschwaben/mp3/192/stream.mp3") //$NON-NLS-1$
                .openStream()) {
            MPEGAudio.decode(is, MPEGAudioContentFilter.MPEG_AUDIO_FRAMES, new MPEGAudioContentHandler() {

                int count = 0;
                BitRate currentBitRate = null;
                Mode currentMode = null;

                SamplingRate currentSamplingRate = null;

                boolean initialDecode = false;

                @SuppressWarnings("nls")
                @Override
                public boolean handle(final MPEGAudioContent content) {
                    final MPEGAudioFrame frame = (MPEGAudioFrame) content;
                    if (!this.initialDecode) {
                        if (StreamInfo.LOG.isInfoEnabled()) {
                            StreamInfo.LOG.info("Detected format: " + frame);
                        }
                        this.initialDecode = true;
                    }
                    if (!frame.getSamplingRate().equals(this.currentSamplingRate)) {
                        if (StreamInfo.LOG.isInfoEnabled()) {
                            StreamInfo.LOG.info("SamplingRate changed: [old: " + this.currentSamplingRate + ", new: "
                                    + frame.getSamplingRate() + "]");
                        }
                        this.currentSamplingRate = frame.getSamplingRate();
                    }
                    if (!frame.getBitRate().equals(this.currentBitRate)) {
                        if (StreamInfo.LOG.isInfoEnabled()) {
                            StreamInfo.LOG.info("BitRate changed: [old: " + this.currentBitRate + ", new: "
                                    + frame.getBitRate() + "]");
                        }
                        this.currentBitRate = frame.getBitRate();
                    }
                    if (!frame.getMode().equals(this.currentMode)) {
                        if (StreamInfo.LOG.isInfoEnabled()) {
                            StreamInfo.LOG.info(
                                    "Mode changed: [old: " + this.currentMode + ", new: " + frame.getMode() + "]");
                        }
                        this.currentMode = frame.getMode();
                    }
                    if ((this.count % 1200) == 0) {
                        System.out.println();
                    }
                    if ((this.count % 10) == 0) {
                        System.out.print(".");
                    }
                    this.count++;
                    return false;
                }
            });
        } catch (final MalformedURLException e) {
            StreamInfo.LOG.error(e.getLocalizedMessage(), e);
        } catch (final IOException e) {
            StreamInfo.LOG.error(e.getLocalizedMessage(), e);
        }

    }

}
