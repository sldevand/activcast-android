package fr.sldevand.activcast.service;

import android.content.Context;
import android.util.SparseArray;
import android.widget.Toast;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class YtUrlResolver {
    private static final int ITAG_720 = 22;
    private static final int ITAG_360 = 18;

    private static OnResolvedUrlListener onResolvedUrlListener;

    public static void resolve(final Context context, String youtubeLink) {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                try {
                    if (null == ytFiles) {
                        throwNotAvailableFormatsException();
                    }

                    YtFile ytFile = ytFiles.get(ITAG_720);
                    if (null == ytFile) {
                        ytFile = ytFiles.get(ITAG_360);
                    }

                    if (null == ytFile) {
                        throwNotAvailableFormatException();
                    }

                    String downloadUrl = ytFile.getUrl();
                    if(null != downloadUrl && null != onResolvedUrlListener) {
                        onResolvedUrlListener.onResolvedUrl(downloadUrl);
                    }

                } catch (Exception exception) {
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.extract(youtubeLink, false, false);
    }

    private static void throwNotAvailableFormatException() throws Exception {
        throw new Exception("There is no available High quality format for this video");
    }

    private static void throwNotAvailableFormatsException() throws Exception {
        throw new Exception("No youtube links could be extracted");
    }

    public void setResolvedUrlListener(OnResolvedUrlListener listener) {
        onResolvedUrlListener = listener;
    }

    public interface OnResolvedUrlListener {
        void onResolvedUrl(String url);
    }
}
